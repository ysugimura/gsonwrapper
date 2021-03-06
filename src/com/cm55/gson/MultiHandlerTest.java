package com.cm55.gson;

import static org.junit.Assert.*;

import org.junit.*;

import com.google.gson.reflect.*;

public class MultiHandlerTest {

  @Before
  public void before() {

  }
  
//  @Test
//  public void invalidTest() {
//    try {
//      Serializer<Bar>serializer = new Serializer<>(InvalidHandler.INSTANCE);
//      fail();
//    } catch (Exception ex) {
//      
//    }
//  }
  
  @Test
  public void TypeTokenのtest() {
    TypeToken<Foo>fooToken = new TypeToken<Foo>() {};
    assertEquals(fooToken.getRawType(), Foo.class);    
  }
  
  @Test
  public void test0() {
    Serializer<Bar>serializer = new Serializer<>(BarHandler.INSTANCE);
    Bar in = new BarTwo();
    String json = serializer.serialize(in);
    //PrintJsonForTest.printJson(json);
    assertEquals("{\"T\":\"BarTwo\",\"D\":{\"b\":2}}", json);
    BarTwo out = (BarTwo)serializer.deserialize(json);

    assertEquals(2, out.b);
  }
  
  @Test
  public void test1() {
    Serializer<Foo>serializer = new Serializer<>(FooHandler.INSTANCE);
    Foo in = new FooTwo();
    String json = serializer.serialize(in);
    //PrintJsonForTest.printJson(json);    
    assertEquals("{\"T\":\"FooTwo\",\"D\":{\"two\":2}}", json);
    Foo out = serializer.deserialize(json);
    assertEquals(2, ((FooTwo)out).two);
    
  }
  
  //@Test
  public void test1_1() {
    String json = "{\"T\":\"FooFour\",\"D\":{\"two\":2}}";
    Serializer<Foo>serializer = new Serializer<Foo>(FooHandler.INSTANCE);
    
    assertNull(serializer.deserialize(json));
    
    serializer.setNullIfClassNotFound(false);
    try {
      serializer.deserialize(json);
      fail();
    } catch (JsonClassNotFoundException ex) {      
    }
  }
  
  @Test 
  public void 抽象クラスが入れ子になっているケース() {
    Serializer<Foo>serializer = new Serializer<Foo>(FooHandler.INSTANCE);
    Foo in = new FooThree(new BarTwo());
    String json = serializer.serialize(in);
    //PrintJsonForTest.printJson(json);
    
    FooThree out = (FooThree)serializer.deserialize(json);
    BarTwo out2 = (BarTwo)out.value;
    assertEquals(2, out2.b);
    
  }
  
  @Test
  public void test3() {
    MultiHandlerBuilder<Bar> handler = new MultiHandlerBuilder<Bar>(Bar.class);
    handler.addSubClass("one",  BarOne.class);
    
    // 同じクラスを別名称で登録しようとしている
    try {
      handler.addSubClass("two",  BarOne.class);
      fail();
    } catch (IllegalArgumentException ex) {      
    }
    
    // 同じ名称で、別のクラスを登録しようとしている。
    try {
      handler.addSubClass("one", BarTwo.class);
      fail();
    } catch (IllegalArgumentException ex) {      
    }
    
    // 同じ名称、同じクラスで登録
    handler.addSubClass("one", BarOne.class);
    
    assertEquals(1, handler.build().subClassCount());
  }
  
  public static class FooHandler  {
    public static final Handler<Foo> INSTANCE = 
        new MultiHandlerBuilder<>(Foo.class).addSubClasses(FooOne.class, FooTwo.class, FooThree.class)
        .addSubHandler(BarHandler.INSTANCE).build();

  }
  
  public static class Foo {    
  }
  
  public static class FooOne extends Foo {
    int one = 1;
  }
  
  public static class FooTwo extends Foo {
    int two = 2;
  }
  
  public static class FooThree extends Foo {
    Bar value;
    public FooThree(Bar value) {
      this.value = value;
    }
  }
  
  public static class InvalidHandler  {
    public static final Handler<Bar> INSTANCE = new HandlerBuilder<>(Bar.class).build();
  }
  
  public static class BarHandler  {
    public static final Handler<Bar> INSTANCE = new MultiHandlerBuilder<Bar>(Bar.class)
        .addSubClasses(BarOne.class, BarTwo.class).build();
  }
  
  public interface Bar {
    
  }
  
  public static class BarOne implements Bar {
    public int a = 1;
  }
  
  public static class BarTwo implements Bar {
    public int b = 2;
  }
  
}
