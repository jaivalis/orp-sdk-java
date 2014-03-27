/*
Copyright (c) 2013, TU Berlin
Permission is hereby granted, free of charge, to any person obtaining 
a copy of this software and associated documentation files (the "Software"),
to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
DEALINGS IN THE SOFTWARE.
*/
package de.dailab.plistacontest.client;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.recsys.Recommender;
import nl.shared.RecommenderItem;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to handle the communication with the plista challenge server
 * Functions:
 *       - parsing incoming HTTP-Requests
 *       - delegating requests according to their types
 *       - responding to the plista challenge server
 * 
 * @author till, andreas
 * 
 */
public class ContestHandler extends AbstractHandler {

    /**
     * Define the default logger
     */
    private final static Logger logger = LoggerFactory.getLogger(ContestHandler.class);

    /**
     * here we store all relevant data about items.
     */
    private final RecommenderItemTable recommenderItemTable = new RecommenderItemTable();
    
    /**
     * Define the default recommender, currently not used.
     */
    private Recommender contestRecommender;


    /**
     * Constructor, sets some default values.
     * @param _properties
     * @param _contestRecommender
     */
    public ContestHandler(final Properties _properties, final Recommender _contestRecommender) {
        this.contestRecommender = _contestRecommender;
    }

    /**
     * Handle incoming messages. This method is called by plista
     * We check the message, and extract the relevant parameter values
     * 
     * @see org.eclipse.jetty.server.Handler#handle(java.lang.String, org.eclipse.jetty.server.Request,
     * javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void handle(String arg0, Request _breq, HttpServletRequest _request, HttpServletResponse _response)
                    throws IOException, ServletException {

    	// we can only handle POST messages
        if (_breq.getMethod().equals("POST")) {
        	
			if (_breq.getContentLength() < 0) {
				// handles first message from the server - returns OK
				System.out.println("[INFO] Initial Message with no content received." );
				response(_response, _breq, null, false);
			}
            else {

            	// handle the normal messages
                String typeMessage = _breq.getParameter("type");
                String bodyMessage = _breq.getParameter("body");
            	
                // we may recode the body message
                if (_breq.getContentType().equals("application/x-www-form-urlencoded; charset=utf-8")) {
                	bodyMessage = URLDecoder.decode(bodyMessage, "utf-8");
                }
                
                
                // delegate the request and create a response message
                // send the response message as text
                String responseText = null;
                responseText = handleMessage(typeMessage, bodyMessage);
                response(_response, _breq, responseText, true);
            }
        }
        else {
            // GET requests are answered by a HTML page
            logger.debug("Get request from " + _breq.getRemoteAddr());
            response(_response, _breq, "Visit <h3><a href=\"http://irml.dailab.de\">CC IRML</a></h3>", true);
        }
    }

    /**
     * Method to handle incoming messages from the server.
     * 
     * @param messageType  the messageType of the incoming contest server message
     * @param _jsonMessageBody  the incoming contest server message
     * @return the response to the contest server
     */
    private String handleMessage(final String messageType, final String _jsonMessageBody) {

        // write all data from the server to a file
        logger.info(messageType + "\t" + _jsonMessageBody);
    	
        // create an jSON object from the String 
        final JSONObject jObj = (JSONObject) JSONValue.parse(_jsonMessageBody);

        // define a response object 
        String response = null;

        System.out.println(messageType);
        // in a complex if/switch statement we handle the differentTypes of messages
        if ("item_update".equalsIgnoreCase(messageType)) {
            
        	// we extract itemID, domainID, text and the timeTime, create/update
        	final RecommenderItem recommenderItem = RecommenderItem.parseItemUpdate(_jsonMessageBody);

//        	// we mark this information in the article table
//        	if (recommenderItem.getItemID() != null) {
//        		recommenderItemTable.handleItemUpdate(recommenderItem);
//        	}
            this.contestRecommender.updateEvent(recommenderItem);
        	response = ";item_update successful";

        }

        else if ("item_create".equalsIgnoreCase(messageType)) {
            final RecommenderItem recommenderItem = RecommenderItem.parseItemUpdate(_jsonMessageBody);
            this.contestRecommender.createEvent(recommenderItem);
        }
        
        else if ("recommendation_request".equalsIgnoreCase(messageType)) {
        	// we handle a recommendation request
            RecommenderItem currentRequest = RecommenderItem.parseRecommendationRequest(_jsonMessageBody);
//        	try {
//        	    // parse the new recommender request
//        		RecommenderItem currentRequest = RecommenderItem.parseRecommendationRequest(_jsonMessageBody);
//
//        		// gather the items to be recommended
//        		List<Long> resultList = recommenderItemTable.getLastItems(currentRequest);
//        		if (resultList == null) {
//        			response = "[]";
//        			System.out.println("invalid resultList");
//        		} else {
//        			response = resultList.toString();
//        		}
//        		response = getRecommendationResultJSON(response);
//
//        	    // TODO? might handle the the request as impressions
//        	} catch (Throwable t) {
//        		t.printStackTrace();
//        	}
            response = this.contestRecommender.getRecommendationJSON(currentRequest);
        }
        else if ("event_notification".equalsIgnoreCase(messageType)) {
        	
        	// parse the type of the event
        	final RecommenderItem item = RecommenderItem.parseEventNotification(_jsonMessageBody);
    		final String eventNotificationType = item.getNotificationType();
    		
            // impression refers to articles read by the user
    		if ("impression".equalsIgnoreCase(eventNotificationType)) {
    			            	                
					// we mark this information in the article table
		        	if (item.getItemID() != null) {
                        // new items shall be added to the list of items
		        		recommenderItemTable.handleItemUpdate(item);
		        	
					response = "handle impression eventNotification successful";
				}
            // click refers to recommendations clicked by the user
    		} else if ("click".equalsIgnoreCase(eventNotificationType)) { 
 
    			response = "handle click eventNotification successful";
    			
    		} else {
    			System.out.println("unknown event-type: " + eventNotificationType + " (message ignored)");
    		}

            this.contestRecommender.notificationEvent(item);
            
        } else if ("error_notification".equalsIgnoreCase(messageType)) {
        	
        	System.out.println("error-notification: " + _jsonMessageBody);
        	
        } else {
        	System.out.println("unknown MessageType: " + messageType);
            // Error handling
            logger.info(jObj.toString());
            //this.contestRecommender.error(jObj.toString());
        }
        return response;
    }

    

    /**
     * Response handler.
     * 
     * @param _response {@link HttpServletResponse} object
     * @param _breq the initial request
     * @param _text response text
     * @param _b  boolean to set whether the response text should be sent
     * @throws IOException
     */
    private void response(HttpServletResponse _response, Request _breq, String _text, boolean _b)
                    throws IOException {
        // configure response parameter
        _response.setContentType("text/html;charset=utf-8");
        _response.setStatus(HttpServletResponse.SC_OK);
        _breq.setHandled(true);

        if (_text != null && _b) {
            _response.getWriter().println(_text);
            if (_text != null && !_text.startsWith("handle")) {
            	System.out.println("[response()] send response: " + _text);
            }
        }
    }
    
	/**
	 * Create a json response object for recommendation requests.
	 * @param _itemsIDs a list as string
	 * @return json
	 */
	public static final String getRecommendationResultJSON(String _itemsIDs) {
		
		// TODO log invalid results
		if (_itemsIDs == null ||_itemsIDs.length() == 0) {
			_itemsIDs = "[]";
		} else if (!_itemsIDs.trim().startsWith("[")) {
			_itemsIDs = "[" + _itemsIDs + "]";
		}
		// build result as JSON according to formal requirements
        String result = "{" + "\"recs\": {" + "\"ints\": {" + "\"3\": "
				+ _itemsIDs + "}" + "}}";

		return result;
	}

}
