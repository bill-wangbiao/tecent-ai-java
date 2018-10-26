package org.alking.tecent.ai;

import org.alking.tecent.ai.domain.Resource;
import org.alking.tecent.ai.face.FaceClient;
import org.alking.tecent.ai.face.FaceClientImpl;
import org.alking.tecent.ai.face.FaceDetechReply;
import org.alking.tecent.ai.impl.OKHttpClient;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class FaceClientImplTest extends BaseTest {

    FaceClientImpl client = new FaceClientImpl(APP_ID,APP_KEY,new OKHttpClient());

    @Test
    public void testFaceDetect() throws IOException {
        String url = "https://raw.githubusercontent.com/aijingsun6/tecent-ai-java/master/doc/face_detect.jpg";
        Resource resource = new Resource(Resource.RES_TYPE_HTTP,url);
        FaceDetechReply reply = client.detect(resource,FaceClient.FACE_DETECT_TYPE_BIG);
        Assert.assertEquals(new Integer(0),reply.getRet());
    }
}