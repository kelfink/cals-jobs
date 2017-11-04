package com.google.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.Message;
import com.google.inject.spi.ModuleAnnotatedMethodScanner;
import com.google.inject.spi.ProvisionListener;
import com.google.inject.spi.TypeConverter;
import com.google.inject.spi.TypeListener;

public class TestBinder<T> implements Binder, AnnotatedBindingBuilder<T> {

  @Override
  public void bindInterceptor(Matcher<? super Class<?>> classMatcher,
      Matcher<? super Method> methodMatcher, MethodInterceptor... interceptors) {}

  @Override
  public void bindScope(Class<? extends Annotation> annotationType, Scope scope) {}

  @Override
  public <T> LinkedBindingBuilder<T> bind(Key<T> key) {
    return null;
  }

  @Override
  public <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral) {
    return null;
  }

  @Override
  public <T> AnnotatedBindingBuilder<T> bind(Class<T> type) {
    return null;
  }

  @Override
  public AnnotatedConstantBindingBuilder bindConstant() {
    return null;
  }

  @Override
  public <T> void requestInjection(TypeLiteral<T> type, T instance) {}

  @Override
  public void requestInjection(Object instance) {}

  @Override
  public void requestStaticInjection(Class<?>... types) {}

  @Override
  public void install(Module module) {}

  @Override
  public Stage currentStage() {
    return null;
  }

  @Override
  public void addError(String message, Object... arguments) {}

  @Override
  public void addError(Throwable t) {}

  @Override
  public void addError(Message message) {}

  @Override
  public <T> Provider<T> getProvider(Key<T> key) {
    return null;
  }

  @Override
  public <T> Provider<T> getProvider(Dependency<T> dependency) {
    return null;
  }

  @Override
  public <T> Provider<T> getProvider(Class<T> type) {
    return null;
  }

  @Override
  public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> typeLiteral) {
    return null;
  }

  @Override
  public <T> MembersInjector<T> getMembersInjector(Class<T> type) {
    return null;
  }

  @Override
  public void convertToTypes(Matcher<? super TypeLiteral<?>> typeMatcher,
      TypeConverter converter) {}

  @Override
  public void bindListener(Matcher<? super TypeLiteral<?>> typeMatcher, TypeListener listener) {}

  @Override
  public void bindListener(Matcher<? super Binding<?>> bindingMatcher,
      ProvisionListener... listeners) {}

  @Override
  public Binder withSource(Object source) {
    return null;
  }

  @Override
  public Binder skipSources(Class... classesToSkip) {
    return null;
  }

  @Override
  public PrivateBinder newPrivateBinder() {
    return null;
  }

  @Override
  public void requireExplicitBindings() {}

  @Override
  public void disableCircularProxies() {}

  @Override
  public void requireAtInjectOnConstructors() {}

  @Override
  public void requireExactBindingAnnotations() {}

  @Override
  public void scanModulesForAnnotatedMethods(ModuleAnnotatedMethodScanner scanner) {}

  @Override
  public ScopedBindingBuilder to(Class<? extends T> implementation) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ScopedBindingBuilder to(TypeLiteral<? extends T> implementation) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ScopedBindingBuilder to(Key<? extends T> targetKey) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void toInstance(T instance) {
    // TODO Auto-generated method stub

  }

  @Override
  public ScopedBindingBuilder toProvider(Provider<? extends T> provider) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ScopedBindingBuilder toProvider(javax.inject.Provider<? extends T> provider) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ScopedBindingBuilder toProvider(
      Class<? extends javax.inject.Provider<? extends T>> providerType) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ScopedBindingBuilder toProvider(
      TypeLiteral<? extends javax.inject.Provider<? extends T>> providerType) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ScopedBindingBuilder toProvider(
      Key<? extends javax.inject.Provider<? extends T>> providerKey) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <S extends T> ScopedBindingBuilder toConstructor(Constructor<S> constructor) {
    return null;
  }

  @Override
  public <S extends T> ScopedBindingBuilder toConstructor(Constructor<S> constructor,
      TypeLiteral<? extends S> type) {
    return null;
  }

  @Override
  public void in(Class<? extends Annotation> scopeAnnotation) {}

  @Override
  public void in(Scope scope) {}

  @Override
  public void asEagerSingleton() {}

  @Override
  public LinkedBindingBuilder<T> annotatedWith(Class<? extends Annotation> annotationType) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public LinkedBindingBuilder<T> annotatedWith(Annotation annotation) {
    // TODO Auto-generated method stub
    return null;
  }

}
