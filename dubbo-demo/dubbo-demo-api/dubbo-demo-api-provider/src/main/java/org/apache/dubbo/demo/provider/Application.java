/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.demo.provider;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.demo.DemoService;

import java.util.concurrent.CountDownLatch;

/**
 * 有些场景中, 比如: 在写SDK的时候, 不能依赖Spring框架.
 * 只能使用API来构建provider和consumer.
 */
public class Application {

    public static void main(String[] args) throws Exception {
        if (isClassic(args)) {
            startWithExport();
        } else {
            startWithBootstrap();
        }
    }

    private static boolean isClassic(String[] args) {
        return args.length > 0 && "classic".equalsIgnoreCase(args[0]);
    }

    /**
     * 单独创建一个服务
     */
    private static void startWithBootstrap() {
        // 创建一个ServiceConfig的实例, 泛型参数是业务接口实现类
        // 即: DemoServiceImpl
        ServiceConfig<DemoServiceImpl> service = new ServiceConfig<>();
        // 指定业务接口
        service.setInterface(DemoService.class);
        // 指定业务接口实现类, 这个对象负责处理consumer的请求
        service.setRef(new DemoServiceImpl());
        // 获取 DubboBootstrap实例. 单例对象. 为什么是单例, 点进去看DubboBootstrap类的注释.
        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        // 生成一个 ApplicationConfig 的实例, 指定ZK地址以及 ServiceConfig实例
        bootstrap.application(new ApplicationConfig("dubbo-demo-api-provider"))
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
                .service(service)
                .start()
                .await();
    }

    /**
     * 暴露服务
     * @throws InterruptedException
     */
    private static void startWithExport() throws InterruptedException {
        ServiceConfig<DemoServiceImpl> service = new ServiceConfig<>();
        service.setInterface(DemoService.class);
        service.setRef(new DemoServiceImpl());
        service.setApplication(new ApplicationConfig("dubbo-demo-api-provider"));
        service.setRegistry(new RegistryConfig("zookeeper://127.0.0.1:2181"));
        service.export();

        System.out.println("dubbo service started");
        new CountDownLatch(1).await();
    }
}
