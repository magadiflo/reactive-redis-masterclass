# Spring Boot 4.1.1 | Reactive Redis Masterclass For Java Spring Boot Developers

---

## 🚀 Cambios Spring Boot `3.5.4` → `4.1.1`

### 📦 Agrega dependencia `redisson-spring-cache` para integrar `Redisson` con `Spring Cache`

En `Spring Boot 3.5.4` utilizábamos únicamente esta dependencia de `Redisson`:

```xml

<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>3.50.0</version>
</dependency>
```

Esta dependencia nos permitía integrar Redisson con Spring Boot y obtener un `RedissonClient` para trabajar con Redis.

En esta primera implementación del curso, además de utilizar Redisson, estamos utilizando la **abstracción de caché de
Spring** mediante las anotaciones: `@Cacheable`, `@CacheEvict`, etc.

Por ejemplo, nuestro servicio de Fibonacci utiliza las siguientes anotaciones:

```java

@Cacheable(cacheNames = "math:fib", key = "#index")
public int getFib(int index) {
    // ...
}

@CacheEvict(cacheNames = "math:fib", key = "#index")
public void clearCache(int index) {
    // ...
}
```

Estas anotaciones pertenecen a `Spring Cache`. Para que Spring pueda utilizar Redisson como implementación de su
`CacheManager`, configuramos:

```java

@Configuration
public class RedissonCacheConfig {

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) {
        return new RedissonSpringCacheManager(redissonClient);
    }
}
```

La clase `RedissonSpringCacheManager` es precisamente el componente que permite conectar la abstracción de
`Spring Cache` con `Redisson`.

Por lo tanto, en `Spring Boot 4.1.1` debemos agregar explícitamente la dependencia `redisson-spring-cache`:

```xml

<dependencies>
    <dependency>
        <groupId>org.redisson</groupId>
        <artifactId>redisson-spring-boot-starter</artifactId>
        <version>4.7.0</version>
    </dependency>

    <dependency>
        <groupId>org.redisson</groupId>
        <artifactId>redisson-spring-cache</artifactId>
        <version>4.7.0</version>
    </dependency>
</dependencies>
```

Esta dependencia proporciona la integración necesaria para utilizar `RedissonSpringCacheManager`.

#### 🔎 ¿Por qué necesitamos esta dependencia?

La dependencia `redisson-spring-cache` **no es necesaria simplemente para utilizar Redisson con Spring Boot**.
Su necesidad aparece porque en esta parte del curso estamos utilizando `Spring Cache`:

```text
@Cacheable
@CacheEvict
     │
     ▼
Spring Cache
     │
     ▼
CacheManager
     │
     ▼
RedissonSpringCacheManager
     │
     ▼
RedissonClient
     │
     ▼
   Redis
```

Es decir, `redisson-spring-cache` nos proporciona la integración entre `Spring Cache` y `Redisson`, permitiendo que las
operaciones realizadas mediante `@Cacheable`, `@CacheEvict`, etc., utilicen Redis a través de Redisson.

En `Spring Boot 3.5.4` esta integración estaba disponible a través de `redisson-spring-boot-starter`, por lo que no
necesitábamos declarar `redisson-spring-cache` de forma independiente.

A partir de la versión utilizada en este proyecto con `Spring Boot 4.1.1` y `Redisson 4.7.0`, esta integración se
encuentra separada, por lo que debemos agregar explícitamente:

```xml

<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-cache</artifactId>
    <version>4.7.0</version>
</dependency>
```

para poder utilizar:

```bash
new RedissonSpringCacheManager(redissonClient)
```

#### 💡 Importante: no siempre necesitamos `redisson-spring-cache`

Esta dependencia **no debe interpretarse como una dependencia obligatoria para utilizar Redisson en Spring Boot**.
Su necesidad depende de la forma en la que utilicemos Redisson. En este ejemplo estamos utilizando `@Cacheable`,
`@CacheEvict`, etc. por lo que necesitamos integrar Redisson con **Spring Cache** mediante `RedissonSpringCacheManager`.
Sin embargo, si posteriormente utilizamos directamente las APIs de Redisson, por ejemplo:

```bash
RBucketReactive<T>
RMapReactive<K, V>
RScoredSortedSetReactive<T>
```

no estamos utilizando la abstracción `Spring Cache` ni necesitamos `RedissonSpringCacheManager`. En esos casos, podemos
utilizar únicamente:

```xml

<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>4.7.0</version>
</dependency>
```

Por tanto:

```text
Redisson directamente
        │
        └── redisson-spring-boot-starter

Spring Cache + Redisson
        │
        ├── redisson-spring-boot-starter
        │
        └── redisson-spring-cache
                │
                └── RedissonSpringCacheManager
```

La dependencia adicional `redisson-spring-cache` es necesaria en esta primera implementación porque estamos utilizando
**las anotaciones de caché de Spring (`@Cacheable`, `@CacheEvict`) y queremos que su almacenamiento sea gestionado por
Redisson/Redis**.

### 🔄 Uso de `Jackson 3` en reemplazo de `Jackson 2`

En `Spring Boot 3.5.4` se utilizaba **Jackson 2**, cuyas principales dependencias pertenecían a:

```text
com.fasterxml.jackson.core:jackson-core
com.fasterxml.jackson.core:jackson-databind
com.fasterxml.jackson.core:jackson-annotations
```

A partir de `Spring Boot 4.0`, **Jackson 3** pasa a ser la biblioteca JSON preferida. Sus principales módulos utilizan
el nuevo namespace `tools.jackson`:

```text
Spring Boot 4.1.1
       │
       ▼
   Jackson 3
       │
       ├── tools.jackson.core:jackson-core
       ├── tools.jackson.core:jackson-databind
       └── com.fasterxml.jackson.core:jackson-annotations
```

Esto puede afectar a librerías externas que todavía utilizan APIs de Jackson 2.

En nuestro caso utilizamos **Redisson 4.7.0**, que mantiene codecs basados tanto en Jackson 2 como en Jackson 3. Por
ejemplo, cuando trabajábamos con Jackson 2 utilizábamos:

```java

@Slf4j
@Service
public class CityService {

    private final CityClient cityClient;
    private final RMapReactive<String, City> cityMap;

    public CityService(CityClient cityClient, RedissonReactiveClient client) {
        this.cityClient = cityClient;
        this.cityMap = client.getMap("city", new TypedJsonJacksonCodec(String.class, City.class));
    }

    /* other codes */
}
```

`TypedJsonJacksonCodec` utiliza APIs de **Jackson 2**, por lo que no es el codec adecuado cuando nuestra aplicación
utiliza Jackson 3.

Redisson proporciona un codec equivalente para Jackson 3: `TypedJsonJackson3Codec`.

Por lo tanto, con `Spring Boot 4.1.1` utilizamos:

```java

@Slf4j
@Service
public class CityService {

    private final CityClient cityClient;
    private final RMapReactive<String, City> cityRMap;

    public CityService(CityClient cityClient, RedissonReactiveClient client) {
        this.cityClient = cityClient;
        this.cityRMap = client.getMap("city", new TypedJsonJackson3Codec(String.class, City.class));
    }

    /* other codes */
}
```

> 💡 **Importante:** cuando una librería externa utilice APIs de Jackson 2, debemos comprobar si dispone de una
> alternativa compatible con Jackson 3 antes de agregar manualmente dependencias de Jackson 2.

De esta manera, podemos utilizar `Spring Boot 4.1.1 + Jackson 3 + Redisson 4.7.0` utilizando los codecs de Redisson
compatibles con `Jackson 3`.
