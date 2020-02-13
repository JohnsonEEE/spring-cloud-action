/*
 *
 *
 * Copyright ( c ) 2020 TH Supcom Corporation. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of TH Supcom
 * Corporation ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with TH Supcom Corporation or a TH Supcom
 * authorized reseller (the "License Agreement"). TH Supcom may make changes to the
 * Confidential Information from time to time. Such Confidential Information may
 * contain errors.
 *
 * EXCEPT AS EXPLICITLY SET FORTH IN THE LICENSE AGREEMENT, TH Supcom DISCLAIMS ALL
 * WARRANTIES, COVENANTS, REPRESENTATIONS, INDEMNITIES, AND GUARANTEES WITH
 * RESPECT TO SOFTWARE AND DOCUMENTATION, WHETHER EXPRESS OR IMPLIED, WRITTEN OR
 * ORAL, STATUTORY OR OTHERWISE INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, TITLE, NON-INFRINGEMENT AND FITNESS FOR A
 * PARTICULAR PURPOSE. TH Supcom DOES NOT WARRANT THAT END USER'S USE OF THE
 * SOFTWARE WILL BE UNINTERRUPTED, ERROR FREE OR SECURE.
 *
 * TH Supcom SHALL NOT BE LIABLE TO END USER, OR ANY OTHER PERSON, CORPORATION OR
 * ENTITY FOR INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY OR CONSEQUENTIAL
 * DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR USE, WHETHER IN AN
 * ACTION IN CONTRACT, TORT OR OTHERWISE, EVEN IF TH Supcom HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES. TH Supcom' TOTAL LIABILITY TO END USER SHALL NOT
 * EXCEED THE AMOUNTS PAID FOR THE TH Supcom SOFTWARE BY END USER DURING THE PRIOR
 * TWELVE (12) MONTHS FROM THE DATE IN WHICH THE CLAIM AROSE.  BECAUSE SOME
 * STATES OR JURISDICTIONS DO NOT ALLOW LIMITATION OR EXCLUSION OF CONSEQUENTIAL
 * OR INCIDENTAL DAMAGES, THE ABOVE LIMITATION MAY NOT APPLY TO END USER.
 *
 * Copyright version 2.0
 */
package org.yiyi.hystrixconsumer.commands;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiyi.hystrixconsumer.api.IHelloService;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * @author yi.yi
 * @date 2020.02.11
 */
public class HelloObservableCommand extends HystrixObservableCommand <String>
{
    private static final Logger s_logger = LoggerFactory.getLogger (HelloObservableCommand.class);

    private IHelloService helloService;

    public HelloObservableCommand (IHelloService helloService)
    {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.helloService = helloService;
    }

    @Override
    protected Observable <String> construct ()
    {
        return Observable.create ((Observable.OnSubscribe <String>) subscriber ->
        {
            try
            {
                if (!subscriber.isUnsubscribed ())
                {
                    subscriber.onNext (helloService.hello ());
                    subscriber.onCompleted ();
                }
            }
            catch (Exception e)
            {
                s_logger.error (e.getLocalizedMessage (), e);
                subscriber.onError (e);
            }
        }).subscribeOn (Schedulers.io ());
    }

    @Override
    protected Observable <String> resumeWithFallback ()
    {
        return Observable.create ((Observable.OnSubscribe <String>) subscriber ->
        {
            try
            {
                if (!subscriber.isUnsubscribed ())
                {
                    subscriber.onNext ("对不起啊！");
                    subscriber.onNext ("兄嘚啊！");
                    subscriber.onCompleted ();
                }
            }
            catch (Exception e)
            {
                s_logger.error (e.getLocalizedMessage (), e);
                subscriber.onError (e);
            }
        }).subscribeOn (Schedulers.io ());
    }
}
