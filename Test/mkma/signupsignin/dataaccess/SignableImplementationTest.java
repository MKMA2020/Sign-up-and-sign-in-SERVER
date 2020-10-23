/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mkma.signupsignin.dataaccess;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import user_message.Message;
import user_message.User;

/**
 *
 * @author Martin Valiente Ainz
 * 
 */
public class SignableImplementationTest {
    
    public SignableImplementationTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of signIn method, of class SignableImplementation.
     */
    @Test
    public void testSignIn() {
        System.out.println("signIn");
        Message message = new Message();
        User user = new User();
        // Login to test
        String login = "";
        // Password to test
        String password = "";
        user.setLogin(login);        
        user.setPassword(password);
        message.setUser(user);
        SignableImplementation instance = new SignableImplementation();
        instance.signIn(message);
    }

    /**
     * Test of signUp method, of class SignableImplementation.
     */
    @Test
    public void testSignUp() {
        System.out.println("signUp");
        Message message = new Message();
        User user = new User();
        // Set user data
        user.setId(0);
        user.setLogin("");
        user.setEmail("");
        user.setFullName("");
        user.setPassword("");
    
        SignableImplementation instance = new SignableImplementation();
        instance.signUp(message);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of signOut method, of class SignableImplementation.
     */
    @Test
    public void testSignOut() {
        System.out.println("signOut");
        Message message = null;
        SignableImplementation instance = new SignableImplementation();
        instance.signOut(message);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
