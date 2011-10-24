package com.griddynamics.spring.nested.autoproxy;

public class CustomerServiceImpl implements CustomerService {
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
