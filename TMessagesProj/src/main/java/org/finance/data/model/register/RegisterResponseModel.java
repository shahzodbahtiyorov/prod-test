package org.finance.data.model.register;

/** @noinspection ALL*/
public class RegisterResponseModel {
    private JwtToken jwt_token;

    public RegisterResponseModel(JwtToken jwt_token) {
        this.jwt_token = jwt_token;
    }

    public JwtToken getJwtToken() {
        return jwt_token;
    }

    public void setJwtToken(JwtToken jwt_token) {
        this.jwt_token = jwt_token;
    }
}

