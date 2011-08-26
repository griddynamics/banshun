package com.macys.platform.util.spring.nested;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.jamonapi.proxy.MonProxyFactory;

public class RegistryBean implements Registry {

	final class ExportBeanTargetSource extends AbstractBeanFactoryBasedTargetSource {
		ExportBeanTargetSource(Class<?> clazz, String name) {
			setTargetClass(clazz);
			setTargetBeanName(name);
		}

		public Object getTarget() throws Exception {
			ExportRef exportRef = exports.get(getTargetBeanName());
			return resolve(getTargetBeanName(),  getTargetClass(), exportRef);
		}
	}

	private Map<String,ExportRef> exports = new LinkedHashMap<String, ExportRef>();
	private boolean tryEagerResolve=false;
	private boolean lookupMonitored=false;
	
	public boolean isTryEagerResolve() {
		return tryEagerResolve;
	}

	public void setTryEagerResolve(boolean tryEagerResolve) {
		this.tryEagerResolve = tryEagerResolve;
	}

    public void setLookupMonitored(final boolean lookupMonitored){
        this.lookupMonitored=lookupMonitored;
    }//setLookupMonitored

    public Void export(ExportRef ref) {
		ExportRef senior = exports.put(ref.getTarget(), ref);
		// TODO validate the senior
		return null; 
	}

    @SuppressWarnings("unchecked")
    public <T> T lookup(final String name, final Class<T> clazz) {
		ExportRef ref = exports.get(name);
		if (tryEagerResolve && ref != null) {
			return resolve(name, clazz, ref);
		} else {
			return (T) ProxyFactory.getProxy(clazz, new ExportBeanTargetSource(clazz, name));
		}
	}//lookupActual

	public <T> Collection<String> lookupByInterface(Class<T> clazz) {
		Collection<String> rez = new ArrayList<String>();
		for(ExportRef ref : exports.values()){
			if(clazz.isAssignableFrom(ref.getInterfaceClass())){
				rez.add(ref.getTarget());
			}
		}
		return rez;
	}

	@SuppressWarnings("unchecked")
	protected <T> T resolve(final String name, final Class<T> clazz,
			ExportRef ref) {
		if(ref ==null){
			throw new NoSuchBeanDefinitionException(name, "can't find export declaration for lookup("+name+","+clazz+")");
		}
		if(!clazz.isAssignableFrom(ref.getInterfaceClass())){
			throw new BeanNotOfRequiredTypeException(name, clazz, ref.getInterfaceClass());
		}
		Object bean=ref.getBeanFactory().getBean(name,clazz);
		if(lookupMonitored){
            return (T)MonProxyFactory.monitor(bean,clazz);
        }else{
            return (T)bean;
        }//if
	}

}//RegistryBean
