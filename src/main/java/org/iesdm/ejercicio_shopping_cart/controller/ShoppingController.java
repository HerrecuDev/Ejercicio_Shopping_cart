package org.iesdm.ejercicio_shopping_cart.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iesdm.ejercicio_shopping_cart.model.Coupon;
import org.iesdm.ejercicio_shopping_cart.model.Order_item;
import org.iesdm.ejercicio_shopping_cart.model.customer_order;
import org.iesdm.ejercicio_shopping_cart.repository.CouponRepository;
import org.iesdm.ejercicio_shopping_cart.repository.CustomerRepository;
import org.iesdm.ejercicio_shopping_cart.repository.OrderRepository;
import org.iesdm.ejercicio_shopping_cart.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/cart")
@SessionAttributes("currentOrder")
@RequiredArgsConstructor
public class ShoppingController {

        private final ProductRepository productRepository;
        private final CouponRepository couponRepository;
        private final CustomerRepository customerRepository;
        private final OrderRepository orderRepository;

        @ModelAttribute("currentOrder")
        public customer_order initOrder() {
            return customer_order.builder()
                    .status(customer_order.Status.PENDING)
                    .grossTotal(BigDecimal.ZERO)
                    .discountTotal(BigDecimal.ZERO)
                    .finalTotal(BigDecimal.ZERO)
                    .build();
        }

        @GetMapping
        public String showCart(@ModelAttribute("currentOrder") customer_order order,
                               Model model) {

            List<Order_item> items = order.getId() == null ?
                    new ArrayList<>() :
                    orderRepository.findByOrderId(order.getId());

            BigDecimal gross = items.stream()
                    .map(Order_item::getLineTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal discount = order.getDiscountTotal() == null
                    ? BigDecimal.ZERO : order.getDiscountTotal();

            BigDecimal total = gross.subtract(discount);

            order.setGrossTotal(gross);
            order.setFinalTotal(total);
            customerRepository.save(order);


            model.addAttribute("id", items);
            model.addAttribute("grossTotal", gross);
            model.addAttribute("discount", discount);
            model.addAttribute("totalToPay", total);
            model.addAttribute("appliedCoupon", order.getCouponId() == null ? null :
                    couponRepository.findById(order.getCouponId())
                            .map(c -> new AppliedCoupon(c.getCode(), discount))
                            .orElse(null));

            return "cart";
        }

        @PostMapping("/add")
        public String addItem(@RequestParam String name,
                              @RequestParam BigDecimal price,
                              @RequestParam int quantity,
                              @ModelAttribute("currentOrder") customer_order order) {

            if (order.getId() == null) {
                customerRepository.save(order);
            }

            Order_item item = Order_item.builder()
                    .orderId(order.getId())
                    .productName(name)
                    .unitPrice(price)
                    .quantity(quantity)
                    .lineTotal(price.multiply(BigDecimal.valueOf(quantity)))
                    .build();

            orderRepository.save(item);
            return "redirect:/cart";
        }

        @PostMapping("/increase")
        public String increase(@RequestParam Integer id) {
            orderRepository.findById(id).ifPresent(i -> {
                i.setQuantity(i.getQuantity() + 1);
                i.setLineTotal(i.getUnitPrice()
                        .multiply(BigDecimal.valueOf(i.getQuantity())));
                orderRepository.save(i);
            });
            return "redirect:/cart";
        }

        @PostMapping("/decrease")
        public String decrease(@RequestParam Integer id) {
            orderRepository.findById(id).ifPresent(i -> {
                int q = i.getQuantity() - 1;
                if (q <= 0) {
                    orderRepository.deleteById(id);
                } else {
                    i.setQuantity(q);
                    i.setLineTotal(i.getUnitPrice()
                            .multiply(BigDecimal.valueOf(q)));
                    orderRepository.save(i);
                }
            });
            return "redirect:/cart";
        }

        @PostMapping("/remove")
        public String remove(@RequestParam Integer id) {
            orderRepository.deleteById(id);
            return "redirect:/cart";
        }

        @PostMapping("/apply-coupon")
        public String applyCoupon(@RequestParam int id,
                                  @ModelAttribute("currentOrder") customer_order order) {

            couponRepository.findById(id).ifPresent(coupon -> {
                order.setCouponId(coupon.getId());
                BigDecimal amount = BigDecimal.valueOf(coupon.getDiscountValue());
                order.setDiscountTotal(amount);
                if (order.getGrossTotal() != null) {
                    order.setFinalTotal(order.getGrossTotal().subtract(amount));
                }
                customerRepository.save(order);
            });
            return "redirect:/cart";
        }

        @GetMapping("/checkout/step2")
        public String step2() {
            return "step2"; // otra plantilla
        }

        @Data
        @AllArgsConstructor
        public static class AppliedCoupon {
            private String code;
            private BigDecimal amount;
        }
    }

