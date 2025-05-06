package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import static java.lang.Math.min;

/**
 * Khớp lệnh cho các lệnh giới hạn (limit order) thông thường.
 */
public class NormalOrderMatchingEngine implements MatchingEngine {

    // =========================================
    //  Các Comparator trợ giúp
    // =========================================

    /** So sánh giá tăng dần tự nhiên (thấp → cao). */
    private static <T extends MatchingEngineInfo> Comparator<T> NATURAL_PRICE_COMPARATOR() {
        return Comparator.comparing(MatchingEngineInfo::getPrice);
    }

    /** So sánh thời gian tăng dần tự nhiên (sớm trước). */
    private static Comparator<OrderInfo> NATURAL_TIME_COMPARATOR() {
        return Comparator.comparing(OrderInfo::getTime);
    }

    /**
     * Bên BÁN giữ nguyên thứ tự tăng dần: giá thấp hơn có lợi cho người mua.
     */
    private static <T extends MatchingEngineInfo> Comparator<T> SELL_PRICE_COMPARATOR() {
        return NATURAL_PRICE_COMPARATOR();
    }

    /**
     * Bên MUA cần ưu tiên giá cao hơn → đảo chiều so sánh.
     */
    private static <T extends MatchingEngineInfo> Comparator<T> BUY_PRICE_COMPARATOR() {
        return NormalOrderMatchingEngine.<T>NATURAL_PRICE_COMPARATOR().reversed();
    }

    /** Hàng đợi lệnh BÁN: giá tốt (tăng dần) rồi đến thời gian. */
    private static Comparator<OrderInfo> SELL_ORDER_COMPARATOR() {
        return NormalOrderMatchingEngine.<OrderInfo>SELL_PRICE_COMPARATOR()
                .thenComparing(NATURAL_TIME_COMPARATOR());
    }

    /** Hàng đợi lệnh MUA: giá tốt (giảm dần) rồi đến thời gian. */
    private static Comparator<OrderInfo> BUY_ORDER_COMPARATOR() {
        return NormalOrderMatchingEngine.<OrderInfo>BUY_PRICE_COMPARATOR()
                .thenComparing(NATURAL_TIME_COMPARATOR());
    }

    // =========================================
    //  Trạng thái sổ lệnh (immutable, đã sắp xếp)
    // =========================================

    /** Độ sâu BID tổng hợp (Top‑N). */
    private final ImmutableSortedSet<TopNInfo> bidOrders;
    /** Độ sâu ASK tổng hợp (Top‑N). */
    private final ImmutableSortedSet<TopNInfo> askOrders;

    /** Danh sách lệnh MUA đơn lẻ (ưu tiên giá‑thời gian). */
    private final ImmutableSortedSet<OrderInfo> buyingOrders;
    /** Danh sách lệnh BÁN đơn lẻ (ưu tiên giá‑thời gian). */
    private final ImmutableSortedSet<OrderInfo> sellingOrders;

    // =========================================
    //  Khởi tạo qua Lombok @Builder
    // =========================================

    @Builder
    private NormalOrderMatchingEngine(Collection<TopNInfo> bidOrders,
                                      Collection<TopNInfo> askOrders,
                                      Collection<OrderInfo> buyingOrders,
                                      Collection<OrderInfo> sellingOrders) {
        /* Phòng thủ – nếu null/empty thì dùng tập rỗng bất biến. */
        this.bidOrders = bidOrders == null || bidOrders.isEmpty()
                ? ImmutableSortedSet.of()
                : ImmutableSortedSet.<TopNInfo>orderedBy(BUY_PRICE_COMPARATOR())
                .addAll(bidOrders)
                .build();

        this.askOrders = askOrders == null || askOrders.isEmpty()
                ? ImmutableSortedSet.of()
                : ImmutableSortedSet.<TopNInfo>orderedBy(SELL_PRICE_COMPARATOR())
                .addAll(askOrders)
                .build();

        this.buyingOrders = buyingOrders == null || buyingOrders.isEmpty()
                ? ImmutableSortedSet.of()
                : ImmutableSortedSet.orderedBy(BUY_ORDER_COMPARATOR())
                .addAll(buyingOrders)
                .build();

        this.sellingOrders = sellingOrders == null || sellingOrders.isEmpty()
                ? ImmutableSortedSet.of()
                : ImmutableSortedSet.orderedBy(SELL_ORDER_COMPARATOR())
                .addAll(sellingOrders)
                .build();
    }

    // =========================================
    //  Logic khớp lệnh
    // =========================================

    /**
     * Xử lý lệnh mới vào sổ.
     *
     * @param newOrderInfo lệnh cần khớp/đặt
     * @return danh sách {@link Decision} cho các khớp lệnh và phần còn dư
     */
    public List<Decision> execute(OrderInfo newOrderInfo) {
        // ---------- Ghi log trạng thái hiện tại ----------
        log.info("bidOrders: {}", bidOrders);
        log.info("askOrders: {}", askOrders);
        log.info("buyingOrders: {}", buyingOrders);
        log.info("sellingOrders: {}", sellingOrders);
        log.info("newOrderInfo: {}", newOrderInfo);

        // Trường hợp nhanh: sổ lệnh trống hoàn toàn → thêm lệnh mới
        if (bidOrders.isEmpty() && askOrders.isEmpty())
            return List.of(PlaceNewOrderDecision.builder()
                    .side(newOrderInfo.getSide())
                    .quantity(newOrderInfo.getQuantity())
                    .price(newOrderInfo.getPrice())
                    .build());

        // Tạo phần tử giả để cắt tập Top‑N tới mức giá của lệnh mới (bao gồm)
        TopNInfo dummy = TopNInfo.builder()
                .price(newOrderInfo.getPrice())
                .build();

        /*
         * 1️⃣ Chọn các mức giá có thể khớp (cross) với lệnh mới.
         *    MUA  → tất cả ASK ≤ buyPrice
         *    BÁN → tất cả BID ≥ sellPrice
         */
        ImmutableSortedSet<TopNInfo> matchingSet = (switch (newOrderInfo.getSide()) {
            case BUY -> askOrders;
            case SELL -> bidOrders;
        }).headSet(dummy, true);

        /*
         * 2️⃣ Lấy hàng đợi lệnh chi tiết ở phía đối ứng.
         *    MUA  → sellingOrders
         *    BÁN → buyingOrders
         */
        ImmutableSortedSet<OrderInfo> orderSet = (switch (newOrderInfo.getSide()) {
            case BUY -> sellingOrders;
            case SELL -> buyingOrders;
        });

        log.info("newOrderInfo: {}, matchingSet: {}, orderSet: {}", newOrderInfo, matchingSet, orderSet);

        // Bản sao có thể thay đổi số lượng còn lại
        OrderInfo consideringOrder = newOrderInfo;
        long remainQty = consideringOrder.getQuantity();
        ImmutableList.Builder<Decision> resultBuilder = ImmutableList.builder();

        // =============================================================
        // 3️⃣ Duyệt qua các mức giá giao cắt (tốt → kém dần)
        // =============================================================
        mainLoop:
        for (TopNInfo matchingElement : matchingSet) {

            BigDecimal topNPrice = matchingElement.getPrice();

            // 3.1️⃣ Lấy tất cả lệnh tại đúng mức giá này (đã được sắp theo thời gian)
            List<OrderInfo> matchingOrders = orderSet.stream()
                    .filter(Objects::nonNull)
                    .filter(o -> o.getPrice().compareTo(topNPrice) == 0)
                    .toList();

            log.info("loop matchingSet: the element: {}, foundMatchingOrders: {}", matchingElement, matchingOrders);

            // 3.2️⃣ Khớp từng lệnh theo ưu tiên giá‑thời gian
            for (OrderInfo matchingOrder : matchingOrders) {

                // Xác định ID lệnh MUA / BÁN cho đối tượng quyết định
                Long buyOrderId = matchingOrder.getSide().isBuy()
                        ? matchingOrder.getOrderId()
                        : consideringOrder.getOrderId();
                Long sellOrderId = matchingOrder.getSide().isSell()
                        ? matchingOrder.getOrderId()
                        : consideringOrder.getOrderId();

                long autoMatchOrderQty = min(consideringOrder.getQuantity(), matchingOrder.getQuantity());
                BigDecimal price = matchingOrder.getPrice();

                resultBuilder.add(AutoMatchDecision.builder()
                        .buyOrderId(buyOrderId)
                        .sellOrderId(sellOrderId)
                        .quantity(autoMatchOrderQty)
                        .price(price)
                        .build());

                // Cập nhật khối lượng còn lại
                remainQty = consideringOrder.getQuantity() - autoMatchOrderQty;
                long topNRemainQty = matchingElement.getQuantity() - autoMatchOrderQty;

                consideringOrder = consideringOrder.toBuilder()
                        .quantity(remainQty)
                        .build();
                matchingElement = matchingElement.toBuilder()
                        .quantity(topNRemainQty)
                        .build();

                // Lệnh mới đã khớp hết → thoát vòng ngoài
                if (remainQty <= 0) break mainLoop;
                // Hết thanh khoản ở mức giá này → chuyển mức kế tiếp
                if (topNRemainQty <= 0) continue mainLoop;
            }

            /*
             * 3.3️⃣ Hết lệnh đối ứng ở mức giá hiện tại nhưng lệnh mới vẫn còn khối lượng →
             *       đặt phần còn lại ở chính mức giá này để giữ ưu tiên giá‑thời gian.
             */
            long placeOrderQty = min(consideringOrder.getQuantity(), matchingElement.getQuantity());

            resultBuilder.add(PlaceNewOrderDecision.builder()
                    .side(consideringOrder.getSide())
                    .quantity(placeOrderQty)
                    .price(consideringOrder.getPrice())
                    .build());

            remainQty = consideringOrder.getQuantity() - placeOrderQty;
            consideringOrder = consideringOrder.toBuilder()
                    .quantity(remainQty)
                    .build();

            if (remainQty <= 0) break;  // Đã hết khối lượng
        }

        // 4️⃣ Khối lượng còn dư chưa khớp cũng chưa được đặt
        if (remainQty > 0) {
            resultBuilder.add(PlaceNewOrderDecision.builder()
                    .side(consideringOrder.getSide())
                    .quantity(remainQty)
                    .price(consideringOrder.getPrice())
                    .build());
        }

        return resultBuilder.build();
    }


