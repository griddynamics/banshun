package com.macys.platform.util.spring.nested;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;


/**
 * just a bean used to carry input parameters for {@link Registry.export}
 * also carries reference to bean factory which are injected by Spring via {@link BeanFactoryAware}
 *  after the way inject beanfactory in the method arguments will be found we can get rid of this class.   
 */
public class ExportRef implements BeanFactoryAware{

	private String target;
	private Class<?> interfaceClass;
	private BeanFactory beanFactory;
	
	public ExportRef() {
	}
	public ExportRef(String target) {
		this.target = target;
	}

	public ExportRef(String target, Class<?> interfaceClass) {
		this.target = target;
		this.interfaceClass = interfaceClass;
	}
	
	public void setTarget(String arg0) {
		target = arg0;
	}
	/**
	 * Used by Spring to inject beanFactory which will be used to resolve reference
	 * */
	public void setBeanFactory(BeanFactory arg0) throws BeansException {
		beanFactory = arg0;
	}
	/** name of the exported service. used for find this export reference by key.
	 * also it used for find the actual service bean in injected bean factory.
	 * <idref> is useful to inject the bean name to this field. */
	public String getTarget() {
		return target;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}
	/** constraint for this reference. requested lookup calls should specify the same 
	 * interface*/
	public Class<?> getInterfaceClass() {
		return interfaceClass;
	}
	public void setInterfaceClass(Class<?> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}
	@Override
	public String toString() {
		return "ExportRef [target=" + target + ", interfaceClass="
				+ interfaceClass + ", beanFactory=" + beanFactory + "]";
	}
	
	
}
