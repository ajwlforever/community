# spring

## IOC-DI

### DI的优点

- 减少粘合代码

	- 依赖注入很方便的注入组件，简化了对组件调用时的代码粘合

- 简化应用程序配置

	- 组件可以很容易的替换，DI能够很轻易的实现基于配置的元数据模式

- 能够在单个存储库中管理常见依赖项

	- DI使得spring维护了一个容器，来管理各组件的生命周期，而不用分散到各个地方

- 改进的可测试性

	- 依赖项（被注入的对象）可以随意替换，能够做到很容易的替换一部分组件已完成测试，如：测试业务模块时替换DAO为模拟

- 培养良好的应用程序设计

	- 与很多组件进行集成，使得只用关系业务代码而不用重复造轮子

### IOC

- 分类

	- 依赖查找

		- 依赖拉取

			- 从注册表（一般是xml文件）中提取依赖项

		- 上下文依赖查找

			- 从容器（一般为Tomcat、spring等）上下文中去查找依赖项

	- 依赖注入（DI）

		- 构造函数依赖注入

			- 如果没有依赖项就无法创建对象

		- setter依赖注入

			- 没有依赖项也可创建对象
			- 接口最好不要使用setter注入默认值，除非必须有一个公共实现或者注入的是公共的配置而不是依赖

- spring中的DI

	- BeanFactory

		- 负责管理组件、包括依赖项以及其生命周期
		- 通过某种配置（BeanDefinition）唯一确定一个bean并为其分配ID或名称并实例化

			- 需实现BeanDefinitionReader接口来读取BeanDefinition

		- 可以通过bean ID和名称从BeanFactory检索一个bean并建立依赖关系

	- BeanDefinition

		- bean的配置，可通过BeanDefinitionReader读取到
		- reader分类

			- PropertiesBeanDefinitionReader

				- 从配置文件中读取

			- XmlBeanDefinitionReader

				- 从xml文件中读取

	- ApplicationContext

		- BeanFactory的扩展实现，除了DI服务还提供了事务和AOP、国际化以及应用程序事件处理等，在开发应用程序时一般通过ApplicationContext与Spring进行交互，在web环境中通过ContextLoaderListener来支持ApplicationContext的启动
		- 分类

			- GenericXmlApplicationContext

				- 基于xml文件启动ApplicationContext

			- AnnotationConfigApplicationContext

				- 基于配置类启动ApplicationContext
				- 在配置类上使用注解@ImportResource可以混合加载xml与@bean来启动ApplicationContext

	- 注入

		- 注入依赖项来源

			- xml
			- 注解

		- 注入方式

			- 字段注入（不推荐）

				- 可能导致违反单一责任原则、导致类臃肿
				- 很难搞清楚哪些是强制需要的依赖项
				- 使用字段注入的类无法单独实例化
				- 无法使用final修饰字段
				- 在编写测试时会带来困难

			- setter注入
			- 构造器注入
			- 方法注入

				- 查找方法注入

					- 当存在两个不同生命周期（如一个为单例一个不为单例）的对象时，其中一个对象需要注入另一对象，则可以使用查找方法注入
					- 查找方法注入会在每次注入时新创建一个被注入的对象

				- 方法替换

					- 动态替换一个bean的某个方法，在修改第三方库的时候有奇效

	- bean实例化

		- 作用域

			- 单例作用域（默认）
			- 原型作用域

				- 在每次获取时创建一个新实例

			- 请求作用域

				- 用于web程序，当为web应用程序使用SpringMVC时，首先针对每个HTTP请求实例化带有请求作用域的bean，然后在请求完成时销毁

			- 会话作用域

				- 用于web程序，当为web应用程序使用SpringMVC时，首先针对每个HTTP会话实例化带有会话作用域的bean，然后在会话结束时销毁

			- 全局会话作用域

				- 基于Portlet的Web应用程序，bean可以在同一个MVC驱动的门户应用程序中的所有Portlet共享

			- 线程作用域

				- 当一个线程请求bean实例时，spring将创建一个新的bean实例，对于同一个线程，返回相同实例

			- 自定义作用域

				- 用户自己实现的作用域

	- 自动装配

		- byName

			- 使用名称进行装配

				- 在使用构造函数注入、setter注入和字段注入时可以使用两种方式进行名称注入

					- @Primary

						- 在有两个相同类型的依赖项时，可通过该注解标识应该首先注入哪个依赖项
						- 只限两个

					- @Autowired + @Qualifier

						- 通过这两个注解唯一确定一个依赖项

		- byType

			- 使用type进行装配，比如说setter注入

		- 构造函数模式

			- 自动注入构造函数中，永远选择匹配项最多的构造函数，当无可匹配的构造函数时，使用byType模式

	- bean的继承

		- 可理解为模板，一个依赖项可以继承另一个依赖项的属性，并覆盖上自己的属性

			- 在xml中需为bean增加parent属性，并写入被继承的模板的id或名称

## bean的生命周期管理

### bean的生命周期

- bean实例化和DI

	- 扫描xml、注解、java配置类中的bean定义
	- 创建bean实例
	- 注入bean依赖项

- 检查Spring Awareness

	- 如果bean类型实现了BeanNameAware，则调用setBeanName（）
	- 如果bean类型实现了BeanClassLoaderAware，则调用setBeanClassLoader（）
	- 如果bean类型实现了ApplicationContextAware，则调用setApplicationContext（）

- 创建bean生命周期回调

	- 如果存在@PostConstruct注解，则使用它注解调用的方法
	- 如果bean类型实现了InitializingBean，则调用afterProertiesSet（）
	- 如果bean定义包含init-method或@Bean(initMethod = "...")则调用初始方法

- 销毁bean生命周期回调

	- 如果存在@preDestroy修饰的方法，则调用该方法
	- 如果bean类型实现了DisposableBean，则调用destroy（）
	- 如果bean定义包含destroy-method或@Bean(destroyMethod="...")则调用该销毁方法

### 创建bean后的初始化方法与销毁bean前的销毁方法

- 使用注解标注方法
- 使用接口
- 使用注解声明方法

### bean感知spring

- 获取名称

	- 实现BeanNameAware接口可让bean获取自己的名称

- 获取上下文

	- 实现ApplicationContextAware接口可让bean获取该bean所在的应用上下文

### 关闭钩子

- 调用AbstractApplicationContext的registerShutdownHook（）方法可以注册一个关闭钩子，关闭钩子会在应用程序关闭时自动执行context的destroy（）方法以触发bean的销毁方法

### factoryBean

- 当无法使用new创建对象时，或者需要创建一个配置好的对象，则可以使用factoryBean，实现了FactoryBean的对象相当于一个对象类型的工厂，可以生成该对象的各种实例

### propertyEditor

- spring内置的用于将字符串转化为对应类型的编辑器
- 也可以通过继承PropertyEditorSupport实现自己的类型转换器

### 事件机制

- 通过继承ApplicationEvent创建事件
- 通过实现ApplicationListener<事件类型>的强类型接口实现监听，该监听在发布被监听类型或子类型的事件时被调用
- 通过ApplicationEventPublisher.publishEvent()注册事件，ApplicationContext扩展了ApplicationEventPublisher接口，所以只需要知道ApplicationContext即可发布事件

### java类配置模式

- @Bean

	- 注册一个bean到spring上下文，实际上是用来生成BeanDefinition

- @Configuration

	- 声明为配置类，相当于xml文件

- @PropertySource

	- 加载配置文件

- @Lazy

	- 在第一次需要使用时才生成该对象

- @Scope

	- 规定该对象的作用域

- @DependsOn

	- 声明该对象依赖的对象，以便spring先生成该对象

- @Autowired

	- 依赖注入

- @ComponentScan

	- 扫描组件

- @Import

	- 加载一个配置类

- @Profile

	- 指定一个配置参数，当jvm参数运行 -Dspring.profiles.active = xxx 时，加载对应的配置项

### 配置文件

- xml中指定profile参数来决定该配置文件为哪种环境
- java类配置中使用@Profile来指定该配置类为哪种环境
- 使用占位符在对应的属性上注入配置文件内的值

## AOP

### aop的类型

- 静态aop

	- 修改应用程序的实际字节码，以达到扩展的目的，如AspectJ的编译时织入

- 动态aop

	- 通过代理的方式在运行时动态的扩展应用程序，优点是在修改切面后不用重新编译应用程序代码，缺点是性能不及静态aop

### 通知类型

- 前置通知

	- 在方法执行前执行的通知
	- 如果前置通知抛出异常，目标方法也不会执行
	- 前置通知可以访问目标方法，目标对象，方法参数
	- 继承MethodBeforeAdvice

- 后置返回通知

	- 在方法执行后执行的通知
	- 如果目标方法抛出异常，则后置返回通知也不会执行
	- 可以访问目标方法，方法参数，目标对象，方法返回值，但是无法进行修改
	- 继承AfterReturningAdvice

- 后置通知

	- 在方法执行后执行的通知
	- 如果目标方法抛出异常，则后置通知仍然会执行
	- 继承AfterAdvice

- 环绕通知

	- 在方法执行前和方法执行后执行的通知，相当于将方法包住，甚至可以选择不执行目标方法
	- 可以访问目标方法，目标对象，方法参数，方法返回值，甚至可以自由修改这些值
	- 继承MethodInterceptor

		- 实现invoke方法

			- 方法参数MethodInvocation

				- 调用getMethod获取方法参数
				- 调用getThis获取目标对象
				- 调用getArgumeents获取方法参数
				- 调用proceed执行目标方法并获取返回值

- 异常通知

	- 在方法抛出异常后执行的通知
	- 可以访问目标方法，目标对象，方法参数，异常
	- 可以根据异常匹配执行相应的通知方法，常用来做异常统一处理机制
	- 继承ThrowsAdvice

		- 实现afterThrowing方法

			- 可选择1参数

				- 异常

			- 可选择4参数

				- 异常，目标方法，方法参数，目标对象

- 引入通知

	- 继承IntroductionInterceptor

### 顾问

- 顾问可以绑定切入点和通知
- 常用实现为DefaultPointcutAdvisor
- 在初始化时传入切入点和通知即可生成顾问，再将顾问通过ProxyFactory的addAdvisor被设置进代理工厂来生效

### 切入点

- spring提供了多种默认切入点，切入点是连接点的集合，举个例子就是被设置为需要AOP的方法的集合
- 原理

	- 切入点提供两个方法

		- getClassFillter

			- ClassFillter用来检查类型是否满足，以判断该类型是否属于切入点

		- getMethodMatcher

			- MethodMatcher方法检查器用来检查方法是否满足，以判断方法是否属于切入点，检查方法有两种，静态检查和动态检查

				- 静态检查

					- 只有两个参数，Method和Class

						- 只要满足Method和Class就可认为可以作为切入点

					- 静态检查执行后会缓存，当下一次相同类型进入时，不用再执行检查，一般情况下都会同时实现静态检查和动态检查，以便提前过滤动态检查需要检查的数量

				- 动态检查

					- 有三个参数，Method，class，args

						- 不但要满足Method、Class，对参数也有动态检查

							- 例：参数int!=0表示只有参数不为0时才执行通知

					- 每次进入时都会再执行检查，所以需要静态检查配合过滤掉那些不用被检查的方法

- 默认实现

	- StaticMethodMatcherPointcut

		- 静态切入点

			- 只需要实现静态检查的切入点
			- 子类

				- NameMatchMethodPointcut

					- 简单名称切入点

						- 直接使用，调用addMethodName来设置方法名，满足的方法名都会执行通知

	- DyanmicMethodMatcherPointcut

		- 动态切入点

			- 只需要实现动态检查的切入点

	- JdkRegexpMethodPointcut

		- jdk实现的正则表达式切入点

			- 直接使用，调用setPattern传入正则表达式，满足正则表达式的方法都会被执行通知

	- AspectJExpressionPointcut

		- AspectJ表达式切入点

			- 直接使用，调用setExpression传入AspectJ表达式，满足表达式的方法都会被执行

	- AnnotationMatchingPointcut

		- 注解切入点

			- 调用forMethodAnnotation传入对应的注解class对象，被该注解修饰的方法被执行通知

	- ComposablePointcut

		- 组合切入点

			- 可以组合多个切入点为一个切入点

				- union方法

					- 可以接受参数classFilter、MethodMatcher、Pointcut
					- 相当于or，满足任意一个条件即可成为切入点

				- intersection方法

					- 可以接受参数classFilter、MethodMathcer、Pointcut
					- 相当于and，必须满足每个被组合的条件才能成为切入点

	- ControlFlowPointcut

		- 控制流切入点

			- 当需要只在某些方法内部调用目标方法时，才执行通知就需要使用控制流切入点
			- 在new切入点时，传入指定的对象与方法名，当调用指定对象的相应方法名并在内部调用目标方法时，通知才会生效
			- 举例：目标对象A,对应的方法test，当调用A.test并且test内部有对被通知对象代理P的调用p.xxx，则xxx方法会执行通知

- 代理

	- JDK动态代理

		- 由jdk实现，当使用jdk代理时，所有方法调用都会被jvm拦截并路由到代理的invoke方法，然后由invoke方法确定是否通知，如果需要通知，则通过反射调用通知链，再调用方法本身
		- 通过ProxyFactory的setInterfaces（该方法由AdvisedSupport提供）方法来指导要代理的接口列表，从而使用jdk代理
		- 在每次调用invoke方法前无法知道该方法是否执行通知，所以无法判断该方法是通知方法还是未通知方法，所以即使是未通知方法也会有额外的反射开销
		- 只能代理接口

	- CGLIB代理

		- CGLIB会为每个代理动态生成新类的字节码，该类是被代理对象的子类，因为在创建新类字节码时会对每个方法询问spring是否是被通知方法，所以不会像jdk动态代理一样无法确认该方法是否为被通知方法
		- CGLIB有两种生成方式

			- 固定通知链

				- 好处是可以减少执行通知链的运行时间开销，相当于将通知直接固定在字节码中，坏处是因为在字节码里写死了，所以无法更改通知链

			- 不固定通知链

				- 可以动态更改通知链，但是效率没有固定的高

		- 能代理接口和类

	- 效率比较

		- 在未固定通知链的CGLIB代理和JDK代理之间，差距不是很明显，jdk代理效率略高一点点
		- 如果使用固定通知链的CGLIB代理，则比JDK代理快得多

	- 如何选择

		- spirng默认的类代理为CGLIB，一般情况下，对应接口，使用JDK代理，对于类，使用CGLIB代理，如果明确知道通知链不会改变，则使用CGLIB的固定通知链代理最好

### 引入

- 可以动态扩展对象的功能，比如说为对象增加一个接口，这个接口不由对象本身实现，而是由代理类实现，因此可以将一些公共接口使用引入功能来实现，因为被实现的接口可能是有状态的（比如接口功能是检查对象是否修改），所以必须每个对象一个独立的引入，不能像通知一样，共用一个通知
- 继承DefaultIntroductionAdvisor并实现相应接口

	- invoke方法

		- DefaultIntroductionAdvisor有默认方法invoke，用来实现引入逻辑，该方法跟环绕通知一样，可以在每次调用目标方法时，执行invoke的逻辑

- 只能对类进行引入，也就是说必须检查类中所有方法
- 使用引入时必须设置CGLIB代理实现，因为引入的原理是用一个新的代理类同时实现目标对象和被扩展的接口，而JDK的代理只能实现接口，会导致代理类没有实现目标对象

### Spring AOP框架

- 使用ProxyFactoryBean

	- ProxyFactoryBean可以设置tarist属性（目标对象）和interceptorNames属性（通知集合或者顾问集合）
	- ProxyFactoryBean实现了FactoryBean，也就是拥有同样的功能，能通过该对象来让spring自动获取目标对象的代理类
	- ProxyFactoryBean也可以进行引入，设置方式与设置顾问相同

		- 当使用引入时，一定要设置ProxyTargetClass为true，指定使用CGLIB的代理方式，否则会有错

	- 可以使用xml进行配置FactoryBean，也可以使用java配置类的形式

- 使用Spring aop 名称空间

	- 在xml配置文件中使用<aop:config>名称空间

		- 在名称空间内使用<aop:pointcut>来声明切入点，并在通知中通过pointcut-ref来注入切入点

			- 声明切入点时可以用and 来声明其他限制

				- 声明参数 ：and args(value)

					- 表示通知能够获取参数value,可以用参数在通知中来做一些判断

				- 声明spring bean name ：and bean(test*)

					- 表示对名称匹配test的springbean，该切入点才生效

		- 使用<aop:aspect>来配置通知

			- 在aspect内部使用<aop:before>来配置前置通知
			- <aop:around>来配置环绕通知
			- <aop:after-returning>来配置后置返回通知
			- <aop:after>来配置后置通知

- 使用@Aspectj样式注解

	- 使用注解的方式来配置aop（最常用）

		- 使用@Aspect注解来声明一个通知类
		- 在通知类中使用@Pointcut来标注void方法来声明切入点
		- 使用@Before、@Around、@After、@AfterReturning，这些注解来声明通知方法
		- 该配置类需要声明@component注解来交给spring容器管理
		- 并且需要在配置类（用@configuration声明）开启@EnableAspectJAutoProxy注解来开启AOP声明式配置

			- 该注解中有proxyTargetClass属性，当设置为ture时使用GCLIB代理，建议打开，原因前面有说
			- 若使用SpringBoot，则可引入spring-boot-starter-aop包来自动配置，可以不用自己来创建配置类

