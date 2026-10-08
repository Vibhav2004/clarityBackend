package com.roadmapapp.roadmapapp.controller;
import java.time.LocalDateTime;

import aj.org.objectweb.asm.commons.JSRInlinerAdapter;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.roadmapapp.roadmapapp.DTO.*;
import com.roadmapapp.roadmapapp.configurations.*;
import com.roadmapapp.roadmapapp.entity.*;
import com.roadmapapp.roadmapapp.repositary.PaymentOrderRepo;
import com.roadmapapp.roadmapapp.repositary.UserRepo;
import com.roadmapapp.roadmapapp.repositary.transactionRepo;
import com.roadmapapp.roadmapapp.service.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;



@RestController
public class Endpoints {
    @Autowired
    private UserService userService;
    @Autowired
    private GeneratedRoadmapService generatedRoadmapService;
    @Autowired
    private StaticStepService staticStepService;
    @Autowired
    private TrackerService  trackerService;
    @Autowired
    private OtpService otpService;
    @Autowired
    private RazorpayClient razorpayClient;
      @Autowired
      private UserRepo userRepo;
    @Autowired
    private
    transactionRepo transactionRepo;
    @Autowired
    private deletedAccountService  deletedAccountService;
    @Autowired
    private  currentSessionService currentSessionService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PaymentOrderRepo paymentOrderRepo;



    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/Register-User")
    public ResponseEntity<User> registerUser(@RequestBody User user) throws Exception {
        SecurityConfig.validateRegisterInput(
                user.getUserName(),
                user.getEmail(),
                user.getPassword()
        );
//        if(user.getPlan()=="PRO"||user.getPlan()=="PREMIUM"){
//            throw new Exception("Don't Try To Sneak Peak Buy The Plan Dude");
//        }
        if ("PRO".equalsIgnoreCase(user.getPlan())
                || "PREMIUM".equalsIgnoreCase(user.getPlan())) {
            throw new Exception("Don't Try To Sneak Peak Buy The Plan Dude");
        }
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/Login-User")
    public ResponseEntity<SessionIDUser> loginUser(@RequestBody User user) {
        // 1. Validate frontend input
        SecurityConfig.validateLoginInput(
                user.getEmail(),
                user.getPassword()
        );
        SessionIDUser newSession= userService.loginUser(user);
        return ResponseEntity.ok(newSession);
    }

    @PostMapping("/LogOut-User")
    public ResponseEntity<?> LogoutUser(@RequestBody currentSessionInfo currentsession ) {

        userService.logOutUser(currentsession);

        return ResponseEntity.ok("User logged out successfully");
    }

        @PostMapping("/Static_Roadmaps/Category")
    public ResponseEntity<?> category(@RequestBody SessionIDUser session) throws Exception {
            System.out.println("Email: " + session.getEmail());
            System.out.println("SessionID: " + session.getSessionID());




        boolean flag=currentSessionService.verifySession(session.getEmail(),session.getSessionID());
        if(flag){
            return ResponseEntity.ok(staticStepService.getCategoryNames());
        }
            return ResponseEntity.status(401)
                    .body("Not Authorized");

    }

    @PostMapping("/Static_Roadmaps/Category/{category}")
    public ResponseEntity<?> getNamesByCategory(
            @PathVariable String category,@RequestBody SessionIDUser session) throws Exception {
        System.out.println("Email: " + session.getEmail());
        System.out.println("SessionID: " + session.getSessionID());
        boolean flag=currentSessionService.verifySession(session.getEmail(),session.getSessionID());
        if(flag){
            return ResponseEntity.ok(
                    staticStepService.getNames(category));
        }
        return ResponseEntity.status(401)
                .body("Not Authorized");
//       // GenerateRoadmapInputValidator.validateCategory(category);


    }


    @PostMapping("/Static_Roadmaps/Category/{category}/{name}/{difficulty}/{email}")
    public ResponseEntity<?> getRoadMap(
            @PathVariable String category,
            @PathVariable String name,
            @PathVariable String difficulty,
            @PathVariable String email,
            @RequestBody Map<String, String> body) throws Exception {

        String sessionId = body.get("sessionID");

        System.out.println("Email: " + email);
        System.out.println("SessionID: " + sessionId);
        boolean flag=currentSessionService.verifySession(email,sessionId);
        if(!flag){
            throw new Exception("Session is Wrong");
        }

        String response = staticStepService.getRoadMap(
                email,
                category,
                name,
                difficulty
        );

        if ("ROADMAP_LIMIT_EXCEEDED".equals(response)) {
            return ResponseEntity
                    .badRequest()
                    .body("You have already generated 3 roadmaps.");
        }

        return ResponseEntity.ok(response);
    }


    @PostMapping("/All_RoadMap/{email}")
    public ResponseEntity<?> getAllRoadMap(
            @PathVariable String email, @RequestBody Map<String, String> body) throws Exception {

        String sessionId = body.get("sessionID");
       boolean flag=currentSessionService.verifySession(email,sessionId);
       if(flag){
           return ResponseEntity.ok(
                   generatedRoadmapService
                           .getAllRoadMaps(email)
           );
       }
       return ResponseEntity.badRequest().build();
    }


@PostMapping("/Profile/{email}")
public ResponseEntity<User> getProfile(
        @PathVariable String email,
        @RequestBody Map<String, String> body) throws Exception {

    String sessionId = body.get("sessionID");

    boolean flag = currentSessionService.verifySession(email, sessionId);

    if (flag) {
        User user = userService.getProfile(email);
        return ResponseEntity.ok(user);
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
}

    @PostMapping("/Custom_Roadmap")
    public ResponseEntity<?> saveRoadmap(
            @RequestBody Map<String, Object> roadmap
    ) {
//        CustomroadmapValidation.validateCustomRoadmap(
//                roadmap
//        );
           User user=userService.findUser((String) roadmap.get("userEmail"));

        // Premium only feature
        if(!"PREMIUM".equalsIgnoreCase(user.getPlan())){
            return ResponseEntity.status(403).body(
                    Map.of(
                            "message",
                            "PREMIUM_REQUIRED"
                    )
            );
        }

        String result =
                generatedRoadmapService
                        .saveCustomRoadmap(roadmap);

        if (
                result.equals(
                        "ROADMAP_ALREADY_EXISTS"
                )
        ) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Roadmap already exists"
                            )
                    );
        }

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Roadmap saved successfully"
                )
        );
    }


@PostMapping("/tracker")
public ResponseEntity<?> saveTrackers(
        @RequestBody TrackerRequest request
) throws Exception {

    String email = request.getEmail();
    String sessionId = request.getSessionID();
System.out.println(email);
    System.out.println(sessionId);
    // 1. Verify session FIRST
    boolean flag = currentSessionService.verifySession(
            email,
            sessionId
    );

    if (!flag) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "message",
                        "Not Authorized"
                ));
    }

    // 2. Get tracker data
    Tracker trackerData = request.getTrackerData();

    if (trackerData == null) {
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "message",
                        "Tracker data is required."
                ));
    }

    // 3. Convert Tracker object -> Map
//    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> tracker =
            objectMapper.convertValue(
                    trackerData,
                    new TypeReference<Map<String, Object>>() {}
            );

//    // 4. Validate tracker data
//    CustomTrackerValidation.validateTracker(tracker);

    // 5. Save ONLY after validation + authorization
    String result = trackerService.saveTracker(tracker);

    // 6. Existing business responses
    if ("TRACKER_ALREADY_EXISTS".equals(result)) {
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "message",
                        "This roadmap is already being tracked."
                ));
    }

    if ("TRACKER_LIMIT_EXCEEDED".equals(result)) {
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "message",
                        "You can only track one roadmap at a time."
                ));
    }

    return ResponseEntity.ok(
            Map.of(
                    "message",
                    "Roadmap added to tracker successfully."
            )
    );
}

    @PostMapping("/all_trackers")
    public ResponseEntity<List<Tracker>> getTrackersByEmail(
            @RequestParam String email,  @RequestBody Map<String, Object> body) throws Exception {

        String sessionId =
                body.get("sessionID").toString();
    System.out.println(email);
        System.out.println(sessionId);
    boolean flag=currentSessionService.verifySession(email,sessionId);
    if(!flag){
        throw new Exception("Session is Invalid");
    }
 EmailInputValidator.validateEmail(email);
        return ResponseEntity.ok(
                trackerService.getTrackersByEmail(email)
        );
    }
    @PostMapping("/Custom_Tracker")
    public ResponseEntity<?> saveCustomTracker(
            @RequestBody Map<String, Object> tracker
    ) {
        //CustomTrackerValidation.validateTracker(tracker);
        User user=userService.findUser((String) tracker.get("userEmail"));
        // Premium only feature
        if(!"PREMIUM".equalsIgnoreCase(user.getPlan())){
            return ResponseEntity.status(403).body(
                    Map.of(
                            "message",
                            "PREMIUM_REQUIRED"
                    )
            );
        }

        try {

            String result =
                    trackerService.saveCustomTracker(
                            tracker
                    );

            if (
                    result.equals(
                            "TRACKER_ALREADY_EXISTS"
                    )
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Tracker already exists"
                                )
                        );
            }

            if (
                    result.equals(
                            "TRACKER_LIMIT_EXCEEDED"
                    )
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Tracker limit exceeded"
                                )
                        );
            }

            return ResponseEntity
                    .ok(
                            Map.of(
                                    "message",
                                    "Tracker saved successfully"
                            )
                    );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }
    @DeleteMapping("/delete_trackers")
    public ResponseEntity<?> deleteTrackers(
            @RequestBody DeleteTrackerRequest request
    ) {
//        EmailInputValidator.validateEmail(email);
        try {
            if (request.getSessionID() == null ||
                    request.getSessionID().isBlank()) {

                return ResponseEntity.badRequest()
                        .body("Session ID is required");
            }
boolean flag=currentSessionService.verifySession(request.getEmail(),request.getSessionID());
if(!flag) {
    throw  new Exception("Not Authorized");
}
            trackerService.deleteTrackers(
                    request.getTrackerIds(),
                    request.getEmail()
            );

            return ResponseEntity.ok(
                    "Trackers deleted successfully"
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Failed to delete trackers"
                    );
        }
    }


//    @PostMapping("/save_tracker")
//    public ResponseEntity<?> saveTracker(
//            @RequestBody TrackerRequest tracker
//    ) throws Exception {
//boolean flag=currentSessionService.verifySession(tracker.getEmail(),tracker.getSessionId());
//if(!flag){
//    throw new Exception("session not found");
//}
//
//       try {
//// 2. Get tracker data
//           Tracker trackerData = tracker.getTrackerData();
//
//           if (trackerData == null) {
//               return ResponseEntity
//                       .badRequest()
//                       .body(Map.of(
//                               "message",
//                               "Tracker data is required."
//                       ));
//           }
//
//           // 3. Convert Tracker object -> Map
////    ObjectMapper objectMapper = new ObjectMapper();
//           Map<String, Object> trackerss =
//                   objectMapper.convertValue(
//                           trackerData,
//                           new TypeReference<Map<String, Object>>() {}
//                   );
//               trackerService.updateTracker(tracker.getTrackerData());
//
//               return ResponseEntity.ok(
//                       "Tracker saved successfully"
//               );
//
//           } catch (Exception e) {
//
//               e.printStackTrace();
//
//               return ResponseEntity
//                       .badRequest()
//                       .body("Failed to save tracker");
//           }
//       }

@PostMapping("/save_tracker")
public ResponseEntity<?> saveTracker(
        @RequestBody TrackerRequest tracker
) {

    try {

        // 1. Basic request validation
        if (tracker == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", "Request body is required."
                    ));
        }

        if (tracker.getEmail() == null || tracker.getEmail().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", "Email is required."
                    ));
        }

        if (tracker.getSessionID() == null || tracker.getSessionID().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", "Session ID is required."
                    ));
        }

        // 2. Verify session
        boolean flag = currentSessionService.verifySession(
                tracker.getEmail(),
                tracker.getSessionID()
        );

        System.out.println("Email = " + tracker.getEmail());
        System.out.println("Session ID = " + tracker.getSessionID());
        System.out.println("Session verified = " + flag);

        if (!flag) {
            return ResponseEntity.status(401)
                    .body(Map.of(
                            "success", false,
                            "message", "Session not found or expired."
                    ));
        }

        // 3. Get tracker data
        Tracker trackerData = tracker.getTrackerData();

        if (trackerData == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", "Tracker data is required."
                    ));
        }

        // 4. Debug tracker data
        System.out.println("========== TRACKER DATA ==========");
        System.out.println("ID = " + trackerData.getId());
        System.out.println("User Email = " + trackerData.getUserEmail());
        System.out.println("Roadmap Name = " + trackerData.getRoadmapName());
        System.out.println("Completed Steps = " + trackerData.getCompletedSteps());
        System.out.println("Total Steps = " + trackerData.getTotalSteps());
        System.out.println("Status = " + trackerData.getStatus());
        System.out.println("==================================");

        // 5. Update tracker
        trackerService.updateTracker(
                trackerData,
                tracker.getEmail()
        );

        // 6. Success
        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Tracker saved successfully."
                )
        );

    } catch (SecurityException e) {

        e.printStackTrace();

        return ResponseEntity.status(403)
                .body(Map.of(
                        "success", false,
                        "message", "Unauthorized tracker access."
                ));

    } catch (Exception e) {

        e.printStackTrace();

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "success", false,
                        "message", e.getMessage() != null
                                ? e.getMessage()
                                : "Failed to save tracker."
                ));
    }
}

    @Value("${razorpay.key.secret}")
    private String secret;

//    @PostMapping("/payment/create-order")
//    public ResponseEntity<?> createOrder(
//            @RequestBody CreateOrderRequest request)
//            throws Exception {
//     System.out.println(request.getEmail());
//        System.out.println(request.getSessionID());
//        long amount;
//      boolean flag=currentSessionService.verifySession(request.getEmail(), request.getSessionID());
//      if(!flag){
//          throw new Exception("Session is Invalid");
//      }
//        if(request.getPlan().equals("PRO")){
//            amount = 9900;
//        } else {
//            amount = 19900;
//        }
//
//        JSONObject orderRequest = new JSONObject();
//
//        orderRequest.put("amount", amount);
//        orderRequest.put("currency", "INR");
//        orderRequest.put("receipt",
//                "receipt_" + System.currentTimeMillis());
//
//
//        Order order =
//                razorpayClient.orders.create(orderRequest);
//
//        return ResponseEntity.ok(order.toString());
//    }

    @PostMapping("/payment/create-order")
    public ResponseEntity<?> createOrder(
            @RequestBody CreateOrderRequest request)
            throws Exception {

        // -----------------------------------------
        // Basic request validation
        // -----------------------------------------

        if (request == null) {
            throw new IllegalArgumentException(
                    "Request is required"
            );
        }

        if (request.getEmail() == null ||
                request.getEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "Email is required"
            );
        }

        if (request.getSessionID() == null ||
                request.getSessionID().isBlank()) {

            throw new IllegalArgumentException(
                    "Session ID is required"
            );
        }

        if (request.getPlan() == null ||
                request.getPlan().isBlank()) {

            throw new IllegalArgumentException(
                    "Plan is required"
            );
        }


        // -----------------------------------------
        // Verify session
        // -----------------------------------------

        boolean flag =
                currentSessionService.verifySession(
                        request.getEmail(),
                        request.getSessionID()
                );

        if (!flag) {
            throw new Exception(
                    "Session is Invalid"
            );
        }


        // -----------------------------------------
        // Normalize plan
        // -----------------------------------------

        String plan =
                request.getPlan()
                        .trim()
                        .toUpperCase();


        // -----------------------------------------
        // Server decides amount
        // -----------------------------------------

        long amount;

        if ("PRO".equals(plan)) {

            amount = 9900L;

        } else if ("PREMIUM".equals(plan)) {

            amount = 19900L;

        } else {

            throw new IllegalArgumentException(
                    "Invalid subscription plan"
            );
        }


        // -----------------------------------------
        // Create Razorpay order
        // -----------------------------------------

        JSONObject orderRequest =
                new JSONObject();

        orderRequest.put(
                "amount",
                amount
        );

        orderRequest.put(
                "currency",
                "INR"
        );

        orderRequest.put(
                "receipt",
                "receipt_" +
                        System.currentTimeMillis()
        );


        Order order =
                razorpayClient.orders.create(
                        orderRequest
                );


        // -----------------------------------------
        // Save payment order in OUR database
        // -----------------------------------------

        PaymentOrder paymentOrder =
                new PaymentOrder();

        paymentOrder.setRazorpayOrderId(
                order.get("id")
        );

        paymentOrder.setEmail(
                request.getEmail()
        );

        paymentOrder.setPlan(
                plan
        );

        paymentOrder.setAmount(
                amount
        );

        paymentOrder.setStatus(
                "PENDING"
        );

        paymentOrderRepo.save(
                paymentOrder
        );


        // -----------------------------------------
        // Return Razorpay order to frontend
        // -----------------------------------------

        return ResponseEntity.ok(
                order.toString()
        );
    }
//
//    @PostMapping("/payment/verify")
//    public ResponseEntity<?> verifyPayment(
//            @RequestBody PaymentVerificationRequest request)
//            throws Exception {
//    boolean flag=currentSessionService.verifySession(request.getEmail(), request.getSessionID());
//    if(!flag){
//        throw new Exception("Session is Invalid");
//    }
//        JSONObject options = new JSONObject();
//
//        options.put(
//                "razorpay_order_id",
//                request.getRazorpayOrderId());
//
//        options.put(
//                "razorpay_payment_id",
//                request.getRazorpayPaymentId());
//
//        options.put(
//                "razorpay_signature",
//                request.getRazorpaySignature());
//
//
//
//        boolean verified =
//                Utils.verifyPaymentSignature(
//                        options,
//                        secret);
//
//        if(!verified){
//            return ResponseEntity.badRequest()
//                    .body("Payment verification failed");
//        }
//
//
//        User user =userService.findUser(request.getEmail());
//
//
//        updateSubscription(user,request,
//                request.getPlan());
//
//        saveTransaction(user,
//                request);
//
//        return ResponseEntity.ok("Payment Success");
//    }

    @PostMapping("/payment/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestBody PaymentVerificationRequest request)
            throws Exception {

        // -----------------------------------------
        // Validate request
        // -----------------------------------------

        if (request == null) {
            throw new IllegalArgumentException(
                    "Request is required"
            );
        }

        if (request.getRazorpayOrderId() == null ||
                request.getRazorpayOrderId().isBlank()) {

            throw new IllegalArgumentException(
                    "Razorpay order ID is required"
            );
        }

        if (request.getRazorpayPaymentId() == null ||
                request.getRazorpayPaymentId().isBlank()) {

            throw new IllegalArgumentException(
                    "Razorpay payment ID is required"
            );
        }

        if (request.getRazorpaySignature() == null ||
                request.getRazorpaySignature().isBlank()) {

            throw new IllegalArgumentException(
                    "Razorpay signature is required"
            );
        }

        if (request.getEmail() == null ||
                request.getEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "Email is required"
            );
        }

        if (request.getSessionID() == null ||
                request.getSessionID().isBlank()) {

            throw new IllegalArgumentException(
                    "Session ID is required"
            );
        }


        // -----------------------------------------
        // Verify logged-in session
        // -----------------------------------------

        boolean flag =
                currentSessionService.verifySession(
                        request.getEmail(),
                        request.getSessionID()
                );

        if (!flag) {
            throw new Exception(
                    "Session is Invalid"
            );
        }


        // -----------------------------------------
        // Find ORIGINAL payment order
        // -----------------------------------------

        PaymentOrder paymentOrder =
                paymentOrderRepo
                        .findByRazorpayOrderId(
                                request.getRazorpayOrderId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Payment order not found"
                                )
                        );


        // -----------------------------------------
        // Verify order belongs to this user
        // -----------------------------------------

        if (!paymentOrder.getEmail()
                .equalsIgnoreCase(
                        request.getEmail()
                )) {

            throw new IllegalArgumentException(
                    "Payment order does not belong to this user"
            );
        }


        // -----------------------------------------
        // Prevent payment replay
        // -----------------------------------------

        if ("PAID".equals(
                paymentOrder.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "Payment has already been processed"
            );
        }


        // -----------------------------------------
        // Verify Razorpay signature
        // -----------------------------------------

        JSONObject options =
                new JSONObject();

        options.put(
                "razorpay_order_id",
                request.getRazorpayOrderId()
        );

        options.put(
                "razorpay_payment_id",
                request.getRazorpayPaymentId()
        );

        options.put(
                "razorpay_signature",
                request.getRazorpaySignature()
        );


        boolean verified =
                Utils.verifyPaymentSignature(
                        options,
                        secret
                );



            if (!verified) {

                paymentOrder.setStatus(
                        "FAILED"
                );

                paymentOrderRepo.save(
                        paymentOrder
                );

                return ResponseEntity.badRequest()
                        .body("Payment verification failed");
            }


        // -----------------------------------------
        // Find user
        // -----------------------------------------

        User user =
                userService.findUser(
                        request.getEmail()
                );


        if (user == null) {

            throw new IllegalArgumentException(
                    "User not found"
            );
        }


        // -----------------------------------------
        // IMPORTANT
        //
        // NEVER use request.getPlan()
        //
        // Plan comes from our database.
        // -----------------------------------------

        String actualPlan =
                paymentOrder.getPlan();


        // -----------------------------------------
        // Activate subscription
        // -----------------------------------------

        updateSubscription(
                user,
                request,
                actualPlan
        );


        // -----------------------------------------
        // Save transaction
        // -----------------------------------------

        saveTransaction(
                user,
                request,
                actualPlan
        );
        if(!"PAID".equals(paymentOrder.getStatus())) {
            paymentOrder.setStatus(
                    "FAILED"
            );
        }

        // -----------------------------------------
        // Mark payment order as PAID
        // -----------------------------------------

        paymentOrder.setStatus(
                "PAID"
        );

        paymentOrderRepo.save(
                paymentOrder
        );


        return ResponseEntity.ok(
                "Payment Success"
        );
    }
    @PostMapping("/payment/failed")
    public ResponseEntity<?> paymentFailed(
            @RequestBody PaymentFailedRequest request
    ) throws Exception {

        boolean flag =
                currentSessionService.verifySession(
                        request.getEmail(),
                        request.getSessionID()
                );

        if(!flag){
            throw new Exception("Invalid Session");
        }

        PaymentOrder paymentOrder =
                paymentOrderRepo
                        .findByRazorpayOrderId(
                                request.getRazorpayOrderId()
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Order not found"
                                )
                        );

        paymentOrder.setStatus(
                "FAILED"
        );

        paymentOrderRepo.save(
                paymentOrder
        );

        return ResponseEntity.ok(
                "FAILED status updated"
        );
    }
//
//    private void updateSubscription(
//            User user,
//            PaymentVerificationRequest request,
//            String plan){
//
//        LocalDateTime now =
//                LocalDateTime.now();
//
//        LocalDateTime expiry;
//
//        if(user.getPlanExpiryDate()!=null &&
//                user.getPlanExpiryDate()
//                        .isAfter(now)){
//
//            expiry =
//                    user.getPlanExpiryDate()
//                            .plusDays(30);
//
//        }else{
//
//            expiry =
//                    now.plusDays(30);
//        }
//
//        user.setSubscriptionActive(true);
//
//        user.setPlanStartDate(now);
//
//        user.setPlanExpiryDate(expiry);
//        user.setLastTransactionId(request.getRazorpayPaymentId());
//
//        if(plan.equals("PRO")){
//
//            user.setPlan("PRO");
//
//            user.setRoadmapLimit(15L);
//
//            user.setTrackerLimit(3L);
//
//        }else{
//
//            user.setPlan("PREMIUM");
//
//            user.setRoadmapLimit(30L);
//
//            user.setTrackerLimit(30L);
//        }
//
//
//        userRepo.save(user);
//    }
private void updateSubscription(
        User user,
        PaymentVerificationRequest request,
        String plan) {

    LocalDateTime now =
            LocalDateTime.now();

    LocalDateTime expiry;


    // -----------------------------------------
    // Calculate expiry
    // -----------------------------------------

    if (user.getPlanExpiryDate() != null &&
            user.getPlanExpiryDate()
                    .isAfter(now)) {

        expiry =
                user.getPlanExpiryDate()
                        .plusDays(30);

    } else {

        expiry =
                now.plusDays(30);
    }


    // -----------------------------------------
    // Apply plan
    // -----------------------------------------

    if ("PRO".equals(plan)) {

        user.setPlan("PRO");

        user.setRoadmapLimit(15L);

        user.setTrackerLimit(3L);

    } else if ("PREMIUM".equals(plan)) {

        user.setPlan("PREMIUM");

        user.setRoadmapLimit(30L);

        user.setTrackerLimit(30L);

    } else {

        throw new IllegalArgumentException(
                "Invalid stored payment plan"
        );
    }


    // -----------------------------------------
    // Subscription information
    // -----------------------------------------

    user.setSubscriptionActive(true);

    user.setPlanStartDate(now);

    user.setPlanExpiryDate(expiry);

    user.setLastTransactionId(
            request.getRazorpayPaymentId()
    );


    // -----------------------------------------
    // Save
    // -----------------------------------------

    userRepo.save(user);
}
//
//    private void saveTransaction(
//            User user,
//            PaymentVerificationRequest request){
//
//        transaction tx =
//                new transaction();
//
//        tx.setUserName(
//                user.getUserName());
//
//        tx.setEmail(
//                user.getEmail());
//
//        tx.setTransactionId(
//                UUID.randomUUID().toString());
//
//        tx.setRazorpayOrderId(
//                request.getRazorpayOrderId());
//
//        tx.setRazorpayPaymentId(
//                request.getRazorpayPaymentId());
//        tx.setActiveSubscription(true);
//        tx.setDurationMonths(1);
////        tx.setPaymentMethod(request.get);
//        if(request.getPlan().equals("PRO")){
//            tx.setPlanName("PRO");
//        }
//        else{
//            tx.setPlanName("Premium");
//        }
//        LocalDateTime now =
//                LocalDateTime.now();
//
//        LocalDateTime expiry;
//
//        if(user.getPlanExpiryDate()!=null &&
//                user.getPlanExpiryDate()
//                        .isAfter(now)){
//
//            expiry =
//                    user.getPlanExpiryDate()
//                            .plusDays(30);
//
//        }else{
//
//            expiry =
//                    now.plusDays(30);
//        }
//        tx.setSubscriptionStartDate(now);
//        tx.setSubscriptionEndDate(expiry);
//        tx.setPlanPurchased(
//                request.getPlan());
//
//        tx.setAmount(
//                request.getPlan()
//                        .equals("PRO")
//                        ? 99L
//                        : 199L);
//
//        tx.setCurrency("INR");
//
//        tx.setTransactionStatus("SUCCESS");
//
//        tx.setTransactionType("SUBSCRIPTION");
//
//        tx.setTransactionDate(
//                LocalDateTime.now());
//
//
//        transactionRepo.save(tx);
//    }

private void saveTransaction(
        User user,
        PaymentVerificationRequest request,
        String plan) {

    transaction tx =
            new transaction();


    // -----------------------------------------
    // User information
    // -----------------------------------------

    tx.setUserName(
            user.getUserName()
    );

    tx.setEmail(
            user.getEmail()
    );


    // -----------------------------------------
    // Internal transaction ID
    // -----------------------------------------

    tx.setTransactionId(
            UUID.randomUUID().toString()
    );


    // -----------------------------------------
    // Razorpay information
    // -----------------------------------------

    tx.setRazorpayOrderId(
            request.getRazorpayOrderId()
    );

    tx.setRazorpayPaymentId(
            request.getRazorpayPaymentId()
    );

    tx.setRazorpaySignature(
            request.getRazorpaySignature()
    );


    // -----------------------------------------
    // Subscription
    // -----------------------------------------

    tx.setActiveSubscription(true);

    tx.setDurationMonths(1);


    // -----------------------------------------
    // Plan + amount
    //
    // IMPORTANT:
    // Plan comes from database.
    // -----------------------------------------

    if ("PRO".equals(plan)) {

        tx.setPlanName("PRO");

        tx.setPlanPurchased("PRO");

        tx.setAmount(99L);

    } else if ("PREMIUM".equals(plan)) {

        tx.setPlanName("PREMIUM");

        tx.setPlanPurchased("PREMIUM");

        tx.setAmount(199L);

    } else {

        throw new IllegalArgumentException(
                "Invalid payment plan"
        );
    }


    // -----------------------------------------
    // Dates
    // -----------------------------------------

    LocalDateTime now =
            LocalDateTime.now();

    LocalDateTime expiry;

    if (user.getPlanExpiryDate() != null &&
            user.getPlanExpiryDate()
                    .isAfter(now)) {

        expiry =
                user.getPlanExpiryDate()
                        .plusDays(30);

    } else {

        expiry =
                now.plusDays(30);
    }


    tx.setSubscriptionStartDate(
            now
    );

    tx.setSubscriptionEndDate(
            expiry
    );


    // -----------------------------------------
    // Payment information
    // -----------------------------------------

    tx.setCurrency("INR");

    tx.setTransactionStatus(
            "SUCCESS"
    );

    tx.setTransactionType(
            "SUBSCRIPTION"
    );

    tx.setTransactionDate(
            now
    );


    // -----------------------------------------
    // Save transaction
    // -----------------------------------------

    transactionRepo.save(tx);
}



    @PostMapping("/plan")
    public ResponseEntity<?> getPlan(
            @RequestBody SessionIDUser sessionIDUser
    ) throws Exception {
//       EmailInputValidator.validateEmail(email);
        boolean flag=currentSessionService.verifySession(sessionIDUser.getEmail(),sessionIDUser.getSessionID());
        if(!flag){
            throw new Exception("Invalid session");
        }
        User user = userService.findUser(sessionIDUser.getEmail());

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body("USER_NOT_FOUND");
        }

        String plan = user.getPlan();

        return ResponseEntity.ok(plan);
    }



    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(
            @RequestBody OtpRequest request
    ) throws Exception {
        // EmailInputValidator.validateEmail(request.getEmail());
        boolean flag = currentSessionService.verifySession(request.getEmail(), request.getSessionId());
        boolean success = false;
        if (flag == true) {
            success = otpService.sendOtp(
                    request.getEmail()
            );
        }


        if (success) {

            return ResponseEntity.ok(
                    "OTP Sent"
            );
        }

        return ResponseEntity.badRequest()
                .body("Failed");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(
            @RequestBody OtpRequest request
    ) {
//        EmailInputValidator.validateEmail(request.getEmail());
        boolean success =
                otpService.verifyOtp(
                        request.getEmail(),
                        request.getOtp()
                );

        if (success) {

            return ResponseEntity.ok(
                    "Verified"
            );
        }

        return ResponseEntity.badRequest()
                .body("Invalid OTP");
    }
    @PostMapping("/editPassword")
    public ResponseEntity<?> editPassword(
            @RequestBody Map<String, Object> body
    ) throws Exception {

        String email =
                body.get("email").toString();

        String password =
                body.get("password").toString();

        String sessionId =
                body.get("sessionID").toString();

        System.out.println(email);
        System.out.println(password);
        System.out.println(sessionId);

        boolean flag =
                currentSessionService.verifySession(
                        email,
                        sessionId
                );

        if (!flag) {
            throw new Exception("Invalid session");
        }

        boolean updated =
                userService.editPassword(
                        email,
                        password
                );

        if (updated) {
            return ResponseEntity.ok(
                    "Password Updated"
            );
        }

        return ResponseEntity.badRequest()
                .body("User Not Found");
    }
    @DeleteMapping("/deleteAccount")
    public ResponseEntity<?> deleteAccount(
            @RequestBody
            DeleteAccountRequest request
    ) throws Exception {
//        EmailInputValidator.validateEmail(request.getEmail());
        boolean flag = currentSessionService.verifySession(request.getEmail(), request.getSessionID());

        String email = request.getEmail();
        boolean deleted = false;
        if (flag) {
            deletedAccountService.logDeletedUser(email);

            deleted = userService.deleteAccount(
                    email
            );
        }


        if (deleted) {
//            deletedAccountService.logDeletedUser(request);
            return ResponseEntity.ok(
                    "Account Deleted Successfully"
            );
        }

        return ResponseEntity
                .badRequest()
                .body(
                        "User Not Found"
                );
    }
}
