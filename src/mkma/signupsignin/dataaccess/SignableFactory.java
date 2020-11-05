package mkma.signupsignin.dataaccess;

import signable.Signable;

/**
 * This class will generate a SignableImplementation object.
 *
 * @author Martin Gros and Martin Valiente
 */
public class SignableFactory {

    /**
     * This method will return a new SignableImplementation Object.
     *
     * @return Will return SignableImplementation Object.
     */
    public Signable getSignable() {
        return (new SignableImplementation());
    }
}
