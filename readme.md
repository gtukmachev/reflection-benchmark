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
TBD
### 2. reflection obj::Class.getMethod + method.invoke(..)
TBD

### 3. reflection + HashMap cache (onj::Class) -> setter method
The same as reflection, but instead of getting a setter each time using the Java reflection 
- I put the methods to a hashmap.

### 4. reflection + ConcurrentHashMap cache (onj::Class) -> setter method
I expect to see some worse results in comparing with the `HashMap`, but, surprisingly - its better!

### 5. when(obj) {is Class1 -> obj.setVal(...) ... }
TBD
## Results:
I was a bit shocked by the achieved results:

The approach `#4` demonstrates the best performance (fastest).
To be honest - I expected to see the best results for the `#1` as the most OOP native solution. 

```bash
                           'Kotlin set field value' (NANO_SECONDS)     min  avg       max
                                                                 .  .    .    .         .
                                   polymorphism (common interface)  :   17   40    138171
               reflection obj::Class.getMethod + method.invoke(..)  :   69  129  12750255
reflection +           HashMap cache (onj::Class) -> setter method  :   27   56    575397
reflection + ConcurrentHashMap cache (onj::Class) -> setter method  :   27   46   4697414
                     when(obj) {is Class1 -> obj.setVal(...) ... }  :   16   31   5981066

              'Kotlin set field value' percentiles (NANO_SECONDS)          100%  90%  80%  70%  60%  50%  40%  30%  20%  10%
                                                                 .  .         .    .    .    .    .    .    .    .    .    .
                                   polymorphism (common interface)  :    138171   46   43   41   40   39   38   37   36   33
               reflection obj::Class.getMethod + method.invoke(..)  :  12750255  126  119  116  114  112  111  110  108  105
reflection +           HashMap cache (onj::Class) -> setter method  :    575397   68   63   60   58   54   48   45   43   40
reflection + ConcurrentHashMap cache (onj::Class) -> setter method  :   4697414   53   49   46   43   40   38   37   36   34
                     when(obj) {is Class1 -> obj.setVal(...) ... }  :   5981066   42   37   34   31   28   26   23   21   19
```