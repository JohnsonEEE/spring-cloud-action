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
package org.yiyi.hystrixconsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.yiyi.hystrixconsumer.api.IHelloService;
import org.yiyi.hystrixconsumer.commands.HelloCommond;
import org.yiyi.hystrixconsumer.commands.HelloObservableCommand;
import rx.Observable;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author yi.yi
 * @date 2020.02.10
 */
@RestController
public class HystrixController
{
    @Autowired
    private IHelloService helloService;

    @RequestMapping (path = "helloExecute", method = RequestMethod.GET)
    public String helloExecute ()
    {
        return new HelloCommond (helloService).execute ();
    }

    @RequestMapping (path = "helloQueue", method = RequestMethod.GET)
    public String helloQueue () throws InterruptedException, ExecutionException
    {
        Future <String> future = new HelloCommond (helloService).queue ();
        Thread.sleep (1000L);
        return future.get ();
    }

    @RequestMapping (path = "helloObserve", method = RequestMethod.GET)
    public String helloObserve ()
    {
        StringBuilder sb = new StringBuilder ("hot -- ");
        Observable <String> observe = new HelloObservableCommand (helloService).observe ();
        observe.subscribe (s -> {
            sb.append (s).append (" -- ");
            System.out.println ("hot --  hey ~ " + s);
        });
        try
        {
            Thread.sleep (1000L);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace ();
        }
        observe.subscribe (s -> {
            sb.append (s).append (" -- ");
            System.out.println ("hot --  hey 2 ~ " + s);
        });
        return sb.toString ();
    }

    @RequestMapping (path = "helloToObserve", method = RequestMethod.GET)
    public String helloToObserve ()
    {
        StringBuilder sb = new StringBuilder ("cold -- ");
        Observable <String> observe = new HelloObservableCommand (helloService).toObservable ();
        observe.subscribe (s -> {
            sb.append (s).append (" -- ");
            System.out.println ("cold --  hey ~ " + s);
        });
        try
        {
            Thread.sleep (1000L);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace ();
        }
        observe.subscribe (s -> {
            sb.append (s).append (" -- ");
            System.out.println ("cold --  hey 2 ~ " + s);
        });
        return sb.toString ();
    }
}
