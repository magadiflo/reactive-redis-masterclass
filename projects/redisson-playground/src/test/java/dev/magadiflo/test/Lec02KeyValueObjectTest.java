package dev.magadiflo.test;

import dev.magadiflo.test.dto.Student;
import dev.magadiflo.test.dto.Teacher;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
class Lec02KeyValueObjectTest extends BaseTest {

    @Test
    void keyValueObjectTest() {
        Student studentToSave = new Student("martin", 36, "Lima");
        RBucketReactive<Student> bucket = this.client.getBucket("student:1");
        Mono<Void> set = bucket.set(studentToSave);
        Mono<Void> get = bucket.get()
                .doOnNext(student -> log.info("{}", student))
                .then();
        StepVerifier.create(set.concatWith(get))
                .verifyComplete();
    }

    @Test
    void keyValueObjectTestJsonJacksonCodec() {
        Teacher teacherToSave = new Teacher("Gabriel", 52);
        RBucketReactive<Teacher> bucket = this.client.getBucket("teacher:1", JsonJacksonCodec.INSTANCE);
        Mono<Void> set = bucket.set(teacherToSave);
        Mono<Void> get = bucket.get()
                .doOnNext(teacher -> log.info("{}", teacher))
                .then();
        StepVerifier.create(set.concatWith(get))
                .verifyComplete();
    }

    @Test
    void keyValueObjectTestJsonJacksonCodec2() {
        Teacher teacherToSave = new Teacher("Gabriel", 52);
        RBucketReactive<Teacher> bucket = this.client.getBucket("teacher:1", new TypedJsonJacksonCodec(Teacher.class));
        Mono<Void> set = bucket.set(teacherToSave);
        Mono<Void> get = bucket.get()
                .doOnNext(teacher -> log.info("{}", teacher))
                .then();
        StepVerifier.create(set.concatWith(get))
                .verifyComplete();
    }


    @Test
    void keyValueObjectTestTypedJsonJacksonCodec() {
        Student studentToSave = new Student("martin", 36, "Lima");
        RBucketReactive<Student> bucket = this.client.getBucket("student:1", new TypedJsonJacksonCodec(Student.class));
        Mono<Void> set = bucket.set(studentToSave);
        Mono<Void> get = bucket.get()
                .doOnNext(student -> log.info("{}", student))
                .then();
        StepVerifier.create(set.concatWith(get))
                .verifyComplete();
    }
}
