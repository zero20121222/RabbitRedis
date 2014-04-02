package com.rabbit.test;

import com.rabbit.util.RabbitUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/rabbit-product.xml",
        "classpath:/spring/rabbit-context.xml"
})
public class RabbitTest {
    @Autowired
    private RabbitUtil rabbitUtil;

    private Integer number = 1;

    @Test
    public void testRabbit() {
        while(true){
            rabbitUtil.sendReliable("my-mq-exchange", number++);
            rabbitUtil.sendReliable("mq-exchange2", number++);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
