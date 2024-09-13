package fi.re.firebackend.config;

//import mul.cam.e.service.BbsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

public class AOPConfig {

    private static final Logger log = LoggerFactory.getLogger(AOPConfig.class);

    public AOPConfig() {
    }

    @Bean
    public AspectClass aspect(){
        return new AspectClass();
    }

    @Aspect
    public static class AspectClass{
//
//        @Around("within(fi.re.firebackend.controller.*) or within(fi.re.firebackend.service.*) or within(fi.re.firebackend.jwt.*)")
//        //@Around("within(mul.cam.e.config.*)")
//        public Object loggerAop(ProceedingJoinPoint joinPoint) throws Throwable {
//            String signatureStr = joinPoint.getSignature().toShortString();
//
//            long startTime = System.currentTimeMillis();
//            //System.out.println(signatureStr + "-실행 전");
//
//            try {
//                System.out.println("loggerAOP:" + signatureStr + " 메소드가 실행되었습니다");
//                Object result = joinPoint.proceed();    // 실행되는 시점
//                return result;
//            }finally {
//                //System.out.println(signatureStr + "-실행 후");
//                //System.out.println("실행 후:" + (System.currentTimeMillis() - startTime));
//            }
//        }

    }

}
