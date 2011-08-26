package com.macys.platform.util.spring.nested;

public class BeanReferenceInfo {

    private String beanName;
    private Class<?> beanInterface;
    private String location;

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanInterface(Class<?> beanInterface) {
        this.beanInterface = beanInterface;
    }

    public Class<?> getBeanInterface() {
        return beanInterface;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeanReferenceInfo that = (BeanReferenceInfo) o;

        if (!beanInterface.equals(that.beanInterface)) return false;
        if (!beanName.equals(that.beanName)) return false;
        if (!location.equals(that.location)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = beanName.hashCode();
        result = 31 * result + beanInterface.hashCode();
        result = 31 * result + location.hashCode();
        return result;
    }
}