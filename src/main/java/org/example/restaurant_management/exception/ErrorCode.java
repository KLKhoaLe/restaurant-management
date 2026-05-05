package org.example.restaurant_management.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)

public enum ErrorCode {
    USER_NOT_FOUND("user not found",1005,HttpStatus.NOT_FOUND),
    UNAUTHORIZED("You do not have permission ",1008, HttpStatus.FORBIDDEN),
    EMAIL_EXISTS("email already exists",1009, HttpStatus.CONFLICT),
    PHONE_EXISTS("phone already exists",1010, HttpStatus.CONFLICT),
    UNAUTHENTICATED("unauthenticated",1010,HttpStatus.UNAUTHORIZED),
    USER_NOT_EXISTED("user not exist",1010,HttpStatus.NOT_FOUND),
    USER_INACTIVE("user inactive",1010,HttpStatus.FORBIDDEN),
    WRONG_PASSWORD("wrong password",1010,HttpStatus.FORBIDDEN),
    INVALID_TOKEN_TYPE("invalid token type",1010,HttpStatus.FORBIDDEN),
    NOT_A_MEMBER("not a member",1010,HttpStatus.FORBIDDEN),
    RESTAURANT_NOT_EXISTED("restaurant not existed",1010,HttpStatus.NOT_FOUND),
    TABLE_NOT_EXISTED("table not existed",1010,HttpStatus.NOT_FOUND),
    TABLE_IN_USE("table in use",1010,HttpStatus.CONFLICT),
    MENU_CATEGORY_HAS_ITEMS("menu category has items",1010,HttpStatus.CONFLICT),
    MENU_CATEGORY_NOT_EXISTED("menu category not existed",1010,HttpStatus.NOT_FOUND),
    MENU_ITEM_NOT_EXISTED("menu item not existed",1010,HttpStatus.NOT_FOUND),
    TABLE_HAS_ACTIVE_ORDER("Table already has an active order",1010,HttpStatus.CONFLICT),
    ORDER_NOT_EXISTED("order not existed",1010,HttpStatus.NOT_FOUND),
    ORDER_NOT_OPEN("order not open",1010,HttpStatus.CONFLICT),
    ORDER_ITEM_NOT_EXISTED("order item not existed",1010,HttpStatus.NOT_FOUND),
    PAYMENT_AMOUNT_MISMATCH("Payment amount does not match order total",1099,HttpStatus.BAD_REQUEST),
    INVALID_INVITE_CODE("invalid invite code", 1010, HttpStatus.NOT_FOUND),
    ALREADY_A_MEMBER("You are already a member of this restaurant",1099,HttpStatus.CONFLICT),
    ORDER_ALREADY_CLOSED( "Order is already closed",1099, HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS_TRANSITION( "Invalid order status transition",1099, HttpStatus.BAD_REQUEST),
    MEMBERSHIP_NOT_EXISTED( "User is not a member of this restaurant",1060, HttpStatus.NOT_FOUND),
    CANNOT_MODIFY_OWNER( "Cannot modify owner's membership",1061, HttpStatus.FORBIDDEN),
    CANNOT_MODIFY_SELF( "Cannot modify your own membership",1062, HttpStatus.FORBIDDEN),
    INSUFFICIENT_PERMISSION( "You don't have permission to perform this action",1063, HttpStatus.FORBIDDEN),
    ;



    ErrorCode(String message, long code , HttpStatusCode statusCode) {
        this.message = message;
        this.code = code;
        this.statusCode = statusCode;
    }

    long code ;
    String message;
    HttpStatusCode statusCode;
}
