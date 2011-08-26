package com.macys.platform.util.spring.nested.xml;

import com.macys.platform.util.system.exceptions.InvalidConfigurationException ;

public class UnknownNestedElementException extends InvalidConfigurationException {

    public UnknownNestedElementException(String message) {
        super(message);
    }
}
