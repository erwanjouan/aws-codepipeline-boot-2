package bluegreen.junit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.GenericTypeResolver;

import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
public class MockitoTest<T> extends Mockito {

    @InjectMocks
    protected T test;

    private final Class<T> beanClass;

    public MockitoTest() {
        // gets the Foo.class out of AbstractTest<Foo>
        this.beanClass = (Class<T>) GenericTypeResolver.resolveTypeArgument(this.getClass(), MockitoTest.class);
    }

    private T newInstance() {
        return null;
    }

    @BeforeEach
    public void initInstance() {
        final T newInstance = this.newInstance();
        this.test = newInstance == null ? Mockito.spy(this.beanClass) : Mockito.spy(newInstance);
        MockitoAnnotations.openMocks(this);
    }

    protected void setField(final String fieldName, final Object value) {
        try {
            final Field field = this.beanClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(field, value);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static <U> ArgumentCaptor<U> captor(final Class<?> clazz) {
        return (ArgumentCaptor<U>) ArgumentCaptor.forClass(clazz);
    }

    public static <U> U mockGeneric(final Class<?> clazz) {
        return (U) Mockito.mock(clazz);
    }

    public T getTest() {
        return this.test;
    }

    public Class<T> getBeanClass() {
        return this.beanClass;
    }

}
