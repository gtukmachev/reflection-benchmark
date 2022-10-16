**todo: finish this document**


# Comparing of different ways to set a value into an object field in Kotlin

## Motivation

## Approaches

How exactly we can solve the problem described above.

1. polymorphism (common interface)
2. reflection obj::Class.getMethod + method.invoke(..)
3. reflection + HashMap cache (onj::Class) -> setter method
4. when(obj) {is Class1 -> obj.setVal(...) ... }

### 1. polymorphism (common interface)

### 2. reflection obj::Class.getMethod + method.invoke(..)

### 3. reflection + HashMap cache (onj::Class) -> setter method

### 4. when(obj) {is Class1 -> obj.setVal(...) ... }

## Results:
I was a bit shocked by the achieved results:

The approach `#4` demonstrates the best performance (fastest).
To be honest - I expected to see the best results for the `#1` as the most OOP native solution. 

```
                 'Kotlin set field value' (NANO_SECONDS)     min  avg      max
                                                       .  .    .    .        .
                         polymorphism (common interface)  :   17   38   132295
     reflection obj::Class.getMethod + method.invoke(..)  :   69  128  7759136
reflection + HashMap cache (onj::Class) -> setter method  :   27   55   842751
           when(obj) {is Class1 -> obj.setVal(...) ... }  :   16   31  5918098
           


    'Kotlin set field value' percentiles (NANO_SECONDS)         100%  90%  80%  70%  60%  50%  40%  30%  20%  10%
                                                       .  .        .    .    .    .    .    .    .    .    .    .
                         polymorphism (common interface)  :   132295   43   40   39   38   38   37   36   35   31
     reflection obj::Class.getMethod + method.invoke(..)  :  7759136  124  118  115  113  111  110  108  106  102
reflection + HashMap cache (onj::Class) -> setter method  :   842751   65   60   58   53   47   45   44   42   40
           when(obj) {is Class1 -> obj.setVal(...) ... }  :  5918098   41   36   34   31   29   26   23   21   20

```