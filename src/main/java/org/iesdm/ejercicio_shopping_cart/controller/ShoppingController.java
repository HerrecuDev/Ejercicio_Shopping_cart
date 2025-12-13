package org.iesdm.ejercicio_shopping_cart.controller;

import lombok.RequiredArgsConstructor;
import org.iesdm.ejercicio_shopping_cart.model.Coupon;
import org.iesdm.ejercicio_shopping_cart.model.CustomerOrder;
import org.iesdm.ejercicio_shopping_cart.model.OrderItem;
import org.iesdm.ejercicio_shopping_cart.model.Product;
import org.iesdm.ejercicio_shopping_cart.repository.CouponRepository;
import org.iesdm.ejercicio_shopping_cart.repository.CustomerRepository;
import org.iesdm.ejercicio_shopping_cart.repository.OrderItemRepository;
import org.iesdm.ejercicio_shopping_cart.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@SessionAttributes("currentOrder")
public class ShoppingController {

    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;

    @ModelAttribute("currentOrder")
    public CustomerOrder initOrder() {


        return CustomerOrder.builder()
                .status(CustomerOrder.Status.PENDING)
                .paymentMethod(CustomerOrder.PaymentMethod.CREDIT_CARD)   // ← aquí
                .paymentStatus(CustomerOrder.PaymentStatus.PENDING)       // ← y aquí
                .grossTotal(BigDecimal.ZERO)
                .discountTotal(BigDecimal.ZERO)
                .finalTotal(BigDecimal.ZERO)
                .build();

    }



    //Listado de los productos

    @GetMapping
    public String showProducts(Model model) {
        List<Product> products = productRepository.findAllActive();
        model.addAttribute("products", products);
        return "products";   // products.html
    }

// Para ver el carrito
    @GetMapping("/cart")
    public String showCart(@ModelAttribute("currentOrder") CustomerOrder order,
                           Model model) {

        List<OrderItem> items = order.getId() == null
                ? new ArrayList<>()
                : orderItemRepository.findByOrderId(order.getId());

        BigDecimal gross = items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = order.getDiscountTotal() == null
                ? BigDecimal.ZERO
                : order.getDiscountTotal();

        BigDecimal total = gross.subtract(discount);

        order.setGrossTotal(gross);
        order.setFinalTotal(total);
        customerRepository.save(order);

        model.addAttribute("items", items);
        model.addAttribute("grossTotal", gross);
        model.addAttribute("discountTotal", discount);
        model.addAttribute("finalTotal", total);

        model.addAttribute("appliedCoupon",
                order.getCouponId() == null ? null :
                        couponRepository.findById(order.getCouponId())
                                .map(c -> new AppliedCoupon(c.getCode(), discount))
                                .orElse(null)
        );

        return "cart";   // cart.html
    }

    //Post para añadir los articulos al carrito
    @PostMapping("/cart/add")
    public String addItem(@RequestParam("productId") Integer productId,
                          @RequestParam("quantity") int quantity,
                          @ModelAttribute("currentOrder") CustomerOrder order) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + productId));

        if (order.getId() == null) {
            order.setStatus(CustomerOrder.Status.PENDING);
            customerRepository.save(order);
        }

        OrderItem item = OrderItem.builder()
                .order_Id(order.getId())
                .product_Id(product.getId())
                .productName(product.getName())
                .unitPrice(product.getPrice())
                .quantity(quantity)
                .lineTotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                .build();

        orderItemRepository.save(item);

        return "redirect:/cart";
    }



    @GetMapping("/checkout")
    public String showCheckout(@ModelAttribute("currentOrder") CustomerOrder order,
                               Model model) {

        // recalcular totales, cargar items si quieres
        List<OrderItem> items = order.getId() == null
                ? List.of()
                : orderItemRepository.findByOrderId(order.getId());

        BigDecimal gross = items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = order.getDiscountTotal() == null
                ? BigDecimal.ZERO
                : order.getDiscountTotal();

        BigDecimal total = gross.subtract(discount);
        order.setGrossTotal(gross);
        order.setFinalTotal(total);

        model.addAttribute("order", order);
        model.addAttribute("items", items);
        model.addAttribute("grossTotal", gross);
        model.addAttribute("discountTotal", discount);
        model.addAttribute("finalTotal", total);

        return "checkout"; // checkout.html en templates
    }

    @PostMapping("/checkout")
    public String processCheckout(@RequestParam String action,
                                  @RequestParam(required = false) String couponCode,
                                  @ModelAttribute("currentOrder") CustomerOrder order,
                                  Model model) {

        // Cargar items y subtotal
        List<OrderItem> items = order.getId() == null
                ? List.of()
                : orderItemRepository.findByOrderId(order.getId());

        BigDecimal gross = items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setGrossTotal(gross);

        if ("applyCoupon".equals(action)) {
            // Buscar cupón por código
            if (couponCode != null && !couponCode.isBlank()) {
                Optional<Coupon> opt = couponRepository.findByCode(couponCode.trim());
                if (opt.isPresent()) {
                    Coupon coupon = opt.get();

                    // Ejemplo simple: porcentaje o cantidad fija
                    BigDecimal discount = BigDecimal.ZERO;
                    if ("percentage".equalsIgnoreCase(coupon.getDiscountType())) {
                        discount = gross
                                .multiply(BigDecimal.valueOf(coupon.getDiscountValue()))
                                .divide(BigDecimal.valueOf(100));
                    } else { // fixed
                        discount = BigDecimal.valueOf(coupon.getDiscountValue());
                    }
                    if (discount.compareTo(gross) > 0) {
                        discount = gross;
                    }

                    order.setCouponId(coupon.getId());
                    order.setDiscountTotal(discount);
                    order.setFinalTotal(gross.subtract(discount));

                    model.addAttribute("appliedCoupon",
                            new AppliedCoupon(coupon.getCode(), discount));
                } else {
                    model.addAttribute("error", "Cupón no válido");
                    order.setCouponId(null);
                    order.setDiscountTotal(BigDecimal.ZERO);
                    order.setFinalTotal(gross);
                }
            }

            customerRepository.save(order);

            model.addAttribute("order", order);
            model.addAttribute("items", items);
            model.addAttribute("grossTotal", order.getGrossTotal());
            model.addAttribute("discountTotal", order.getDiscountTotal());
            model.addAttribute("finalTotal", order.getFinalTotal());
            model.addAttribute("couponCode", couponCode);

            return "checkout"; // se queda en la misma página
        }

        // "continueToPayment"
        if (order.getDiscountTotal() == null) {
            order.setDiscountTotal(BigDecimal.ZERO);
        }
        order.setFinalTotal(order.getGrossTotal().subtract(order.getDiscountTotal()));
        order.setStatus(CustomerOrder.Status.PENDING);
        customerRepository.save(order);

        return "redirect:/payment";
    }


    //Ahora realizamos la parte del pago:

    @GetMapping("/payment")
    public String showPayment(@ModelAttribute("currentOrder") CustomerOrder order,
                              Model model) {

        // Por si acaso, recalculamos totales
        List<OrderItem> items = order.getId() == null
                ? List.of()
                : orderItemRepository.findByOrderId(order.getId());

        BigDecimal gross = items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = order.getDiscountTotal() == null
                ? BigDecimal.ZERO
                : order.getDiscountTotal();

        order.setGrossTotal(gross);
        order.setFinalTotal(gross.subtract(discount));

        model.addAttribute("order", order);
        model.addAttribute("items", items);

        return "payment"; // payment.html
    }

    @PostMapping("/payment")
    public String processPayment(@ModelAttribute("currentOrder") CustomerOrder order,
                                 @RequestParam("paymentMethod") CustomerOrder.PaymentMethod method,
                                 @RequestParam(value = "paymentDetails", required = false) String details,
                                 Model model) {

        order.setPaymentMethod(method);
        order.setPaymentStatus(CustomerOrder.PaymentStatus.COMPLETED);
        order.setPaymentDetails(details);
        order.setStatus(CustomerOrder.Status.COMPLETED);

        customerRepository.save(order);

        List<OrderItem> items = order.getId() == null
                ? List.of()
                : orderItemRepository.findByOrderId(order.getId());

        model.addAttribute("order", order);
        model.addAttribute("items", items);

        return "order-confirmation"; // order-confirmation.html
    }



}


