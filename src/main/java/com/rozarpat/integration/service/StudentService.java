package com.rozarpat.integration.service;

import java.util.Map;
import java.util.Random;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.rozarpat.integration.entity.StudentOrder;
import com.rozarpat.integration.repository.StudentOrderRepository;

@Service
public class StudentService {

	@Autowired
	private StudentOrderRepository orderRepository;

	@Value("${rozarpay.key.id}")
	private String rozarPayKey;

	@Value("${rozarpay.secret.key}")
	private String rozarPaySecret;

	private RazorpayClient client;
	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	public void StudentService() throws RazorpayException {
		this.client = new RazorpayClient(rozarPayKey, rozarPaySecret);
	}

	public StudentOrder createOrder(StudentOrder stuOrder) throws RazorpayException {
		JSONObject orderReq = new JSONObject();
		orderReq.put("amount", stuOrder.getAmount() * 100); // amount in paisa
		orderReq.put("currency", "INR");

		// Generate a numeric order ID
		long orderId = System.currentTimeMillis() + new Random().nextInt(1000); // current time in milliseconds + random
																				// number
		orderReq.put("receipt", stuOrder.getEmail()); // use email as receipt

		// create order in Razorpay
		Order razarPayOrder = client.orders.create(orderReq);
		System.out.println(razarPayOrder);

		stuOrder.setRozarpayOrderId(razarPayOrder.get("id"));
		stuOrder.setOrderStatus(razarPayOrder.get("status"));
		stuOrder.setReciept(stuOrder.getEmail()); // set the receipt value to email
		orderRepository.save(stuOrder);
		sendOrderConfirmationEmail(stuOrder);

		return stuOrder;
	}

	public StudentOrder updateOrder(Map<String, String> responsePayLoad) {
		System.out.println("Response Payload: " + responsePayLoad);
		String razorPayOrderId = responsePayLoad.get("razorpay_order_id");

		if (razorPayOrderId == null) {
			throw new RuntimeException("razorpay_order_id is missing in the response payload");
		}

		StudentOrder order = orderRepository.findByRozarpayOrderId(razorPayOrderId);
		if (order != null) {
			order.setOrderStatus("PAYMENT_COMPLETED");
			return orderRepository.save(order);
		} else {

			throw new RuntimeException("Order not found for ID: " + razorPayOrderId);
		}
	}

	private void sendOrderConfirmationEmail(StudentOrder stuOrder) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(stuOrder.getEmail());
        message.setSubject("Order Confirmation");
        message.setText("Dear " + stuOrder.getName() + ",\n\n" +
                "Thank you for your order. Your order ID is " + stuOrder.getRozarpayOrderId() + ".\n\n" +
                "Course: " + stuOrder.getCourse() + "\n" +
                "Amount: " + stuOrder.getAmount() + "\n\n" +
                "We have received your order and will process it shortly.\n\n" +
                "Best regards,\n" +
                "Your Company Name");

        mailSender.send(message);
    }
}
