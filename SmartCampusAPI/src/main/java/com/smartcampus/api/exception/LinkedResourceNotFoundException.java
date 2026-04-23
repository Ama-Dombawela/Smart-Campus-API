/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.exception;

/**
 * @author Ama Dombawela
 * UOW No: W2120682
 * IIT Student No: 20231642
 *
 * Linked Resource Not Found Exception - Thrown when a referenced resource does not exist
 */
public class LinkedResourceNotFoundException extends RuntimeException {

    public LinkedResourceNotFoundException(String message) {
        super(message);
    }

}
