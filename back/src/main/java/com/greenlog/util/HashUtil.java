/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.greenlog.util;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author Cansei2
 */

public class HashUtil {
    
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String hash(String senha) {
        return encoder.encode(senha);
    }

    public static boolean matches(String senha, String hash) {
        return encoder.matches(senha, hash);
    }
}
