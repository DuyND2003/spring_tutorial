package com.example.demo;

import com.example.demo.entity.OrderBook;
import com.example.demo.entity.OrderTransaction;
import com.example.demo.repo.OrderBookRepository;
import com.example.demo.repo.OrderTransactionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;


@Service
public class OrderService {
    private final OrderBookRepository orderRepo;
    private final OrderTransactionRepository txRepo;

    public OrderService(OrderBookRepository orderRepo, OrderTransactionRepository txRepo) {
        this.orderRepo = orderRepo;
        this.txRepo = txRepo;
    }

    public List<OrderBook> getAllOrders() { return orderRepo.findAll(); }
    public List<OrderBook> getBuys() { return orderRepo.findBySide("BUY"); }
    public List<OrderBook> getSells() { return orderRepo.findBySide("SELL"); }
    public List<OrderBook> getBestBid() { return orderRepo.bestBid(PageRequest.of(0, 1)); }

    public void matchOrders() {
        // Bước 1: Lấy toàn bộ lệnh mua và lệnh bán từ database
        List<OrderBook> buyOrders = orderRepo.findBySide("BUY");
        List<OrderBook> sellOrders = orderRepo.findBySide("SELL");

        // Bước 2: Sắp xếp
        // - Lệnh mua: giá cao -> thấp (muốn mua giá cao hơn)
        buyOrders.sort(Comparator.comparing(OrderBook::getPrice).reversed());
        // - Lệnh bán: giá thấp -> cao (muốn bán giá thấp hơn trước)
        sellOrders.sort(Comparator.comparing(OrderBook::getPrice));

        // Bước 3: Đưa các lệnh vào Priority Queue để tối ưu thao tác lấy lệnh tốt nhất
        Queue<OrderBook> buyQueue = new PriorityQueue<>(Comparator.comparing(OrderBook::getPrice).reversed());
        Queue<OrderBook> sellQueue = new PriorityQueue<>(Comparator.comparing(OrderBook::getPrice));
        buyQueue.addAll(buyOrders);
        sellQueue.addAll(sellOrders);

        // Bước 4: Bắt đầu vòng lặp khớp lệnh
        while (!buyQueue.isEmpty() && !sellQueue.isEmpty()) {
            // Lấy lệnh mua giá cao nhất và lệnh bán giá thấp nhất
            OrderBook buyOrder = buyQueue.peek();
            OrderBook sellOrder = sellQueue.peek();

            // Kiểm tra xem có thể khớp lệnh hay không
            if (buyOrder.getPrice() >= sellOrder.getPrice()) {
                // Bước 5: Thực hiện khớp lệnh

                // Xác định giá khớp là giá của lệnh bán (có thể thay đổi tùy theo cơ chế)
                BigDecimal price = new BigDecimal(sellOrder.getPrice());

                // Xác định số lượng khớp: lấy min giữa lệnh mua và bán
                BigDecimal quantity = sellOrder.getQuantity().min(buyOrder.getQuantity());

                // Bước 6: Ghi nhận giao dịch vào bảng OrderTransaction
                OrderTransaction transaction = new OrderTransaction();
                transaction.setOrder(buyOrder);      // (hoặc thiết kế là lưu luôn cả Buy + Sell tùy yêu cầu)
                transaction.setPrice(price);
                transaction.setQuantity(quantity);
                txRepo.save(transaction); // Lưu giao dịch vào DB

                // Bước 7: Cập nhật lại số lượng còn lại của lệnh mua và bán
                buyOrder.setQuantity(buyOrder.getQuantity().subtract(quantity));
                sellOrder.setQuantity(sellOrder.getQuantity().subtract(quantity));

                // Bước 8: Nếu lệnh bán đã khớp hết -> Xóa khỏi queue
                if (sellOrder.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                    sellQueue.poll();
                }

                // Nếu lệnh mua đã khớp hết -> Xóa khỏi queue
                if (buyOrder.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                    buyQueue.poll();
                }

                // Bước 9: Lưu lại trạng thái mới của lệnh mua và lệnh bán vào DB
                orderRepo.save(buyOrder);
                orderRepo.save(sellOrder);

            } else {
                // Nếu không thể khớp (giá mua < giá bán) -> Dừng vòng lặp
                break;
            }
        }
    }}