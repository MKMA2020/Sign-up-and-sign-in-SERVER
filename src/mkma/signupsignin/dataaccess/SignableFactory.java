/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mkma.signupsignin.dataaccess;

import signable.Signable;

/**
 * This class will generate a SignableImplementation object.
 * @author Martin Gros and Martin Valiente
 */
public class SignableFactory {

    public Signable SignableFactory() {
        return (new SignableImplementation());
    }
}
