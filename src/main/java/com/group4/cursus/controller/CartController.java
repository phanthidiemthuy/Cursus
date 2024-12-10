package com.group4.cursus.controller;

import com.group4.cursus.dto.CartItemDTO;
import com.group4.cursus.dto.MyCustomResponse;
import com.group4.cursus.entity.Cart;
import com.group4.cursus.entity.Course;
import com.group4.cursus.entity.Student;
import com.group4.cursus.repository.CartRepository;
import com.group4.cursus.repository.CourseRepository;
import com.group4.cursus.repository.EnrollmentRepository;
import com.group4.cursus.repository.StudentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("users/cart")
public class CartController {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @PostMapping("/add/{courseId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MyCustomResponse> addToCart(@PathVariable Long courseId, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("studentId");
            Student student = studentRepository.findById(Math.toIntExact(userId)).orElseThrow();
            Course course = courseRepository.findById(courseId).orElseThrow();

            boolean inCart = cartRepository.checkIfCourseInCart(userId, courseId) > 0;
            boolean purchased = enrollmentRepository.existsByStudentUserIdAndCourseCourseId(userId, courseId);

            if (inCart) {
                return ResponseEntity.badRequest()
                        .body(new MyCustomResponse("Khóa học đã có trong giỏ hàng", false));
            }
            if (purchased) {
                return ResponseEntity.badRequest()
                        .body(new MyCustomResponse("Khóa học đã được mua", false));
            }

            Cart cart = new Cart();
            cart.setAddingDate(LocalDate.now());
            cart.setDescription(course.getDescription());
            cart.setPrice(course.getRegularPrice());
            cart.setCourse(course);
            cart.setStudent(student);

            cartRepository.save(cart);
            return ResponseEntity.ok(new MyCustomResponse("Khóa học đã được thêm vào giỏ hàng", true));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể thêm vào giỏ hàng", e);
        }
    }

    @GetMapping("/status/c/{courseId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Boolean> checkUserCartItem(@PathVariable Long courseId, HttpSession session) {
        Long userId = (Long) session.getAttribute("studentId");
        boolean inCart = cartRepository.checkIfCourseInCart(userId, courseId) > 0;
        return Collections.singletonMap("inCart", inCart);
    }


    @GetMapping("/mine")
    @ResponseStatus(HttpStatus.OK)
    public Page<CartItemDTO> getAllMyCartItems(@RequestParam(defaultValue = "0") Integer page, HttpSession session) {
        Long userId = (Long) session.getAttribute("studentId");
        Pageable pageable = PageRequest.of(Math.abs(page), 5);
        Page<Cart> cartsPage = cartRepository.findByStudentUserId(userId, pageable);
        return cartsPage.map(this::convertToCartItemDTO);
    }


    @GetMapping(path = "/mine/bill")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, BigDecimal> getMyCartBill(HttpSession session) {
        Long userId = (Long) session.getAttribute("studentId");
        BigDecimal totalPrice = cartRepository.getTotalBillForUser(userId);
        return Collections.singletonMap("totalPrice", totalPrice);
    }

    @GetMapping(path = "/mine/count")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Long> countMyCartItems(HttpSession session) {
        Long userId = (Long) session.getAttribute("studentId");
        long cartCount = cartRepository.countCartByStudentUserId(userId);
        return Collections.singletonMap("cartCount", cartCount);
    }

    @DeleteMapping(path = "/course/{courseId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MyCustomResponse> removeCartByCourseId(@PathVariable Long courseId, HttpSession session) {
        Long userId = (Long) session.getAttribute("studentId");
        int deletedCount = cartRepository.deleteByStudentUserIdAndCourseCourseId(Math.toIntExact(userId), courseId);
        if (deletedCount != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not remove from cart");
        }
        return ResponseEntity.ok(new MyCustomResponse("Removed from Cart, course " + courseId, true));
    }


    private CartItemDTO convertToCartItemDTO(Cart cart) {
        Course course = cart.getCourse();
        return new CartItemDTO(
                course.getCourseId(),
                course.getCourseTitle(),
                course.getDescription(),
                cart.getPrice()
        );
    }
}
