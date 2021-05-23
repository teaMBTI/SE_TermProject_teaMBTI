package com.seproject.mbtimatchingsystem;

import junit.framework.TestCase;

import org.junit.Test;

public class LoginUnitTest extends TestCase {

    private LoginActivity loginActivity;

    public void setUp() {
        loginActivity = new LoginActivity();
    }

    @Test
    public void testId() {
        String email = loginActivity.isValidEmail("harry@google.com");
        assertEquals("Email Valid", email);
    }

    @Test
    public void testPassword() {
        String password = loginActivity.isValidPassword("123456");
        assertEquals("Password Valid", password);
    }

    @Test
    public void testLoginResult() {
        String result = loginActivity.loginCheck("harry@google.com", "123456");
        assertEquals("Login Success", result);
    }
}
