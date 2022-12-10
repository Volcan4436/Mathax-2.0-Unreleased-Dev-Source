package mathax.client.init;

public @interface PostInit {
    Class<?>[] dependencies() default { };
}