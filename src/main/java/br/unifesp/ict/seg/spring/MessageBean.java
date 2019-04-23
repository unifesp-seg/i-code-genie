package br.unifesp.ict.seg.spring;

import java.time.LocalTime;
import org.springframework.stereotype.Service;

import br.unifesp.ict.seg.geniesearchapi.infrastructure.util.GenieSearchAPIConfig;

@Service
public class MessageBean {

    public String getMessage() {
        int numDocs = 0;
    	try {
    		numDocs = GenieSearchAPIConfig.getSolrNumDocs();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  numDocs + " Button was clicked at " + LocalTime.now();

    }
}
