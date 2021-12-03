package com.changgou.token;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

/*****
 * @Author: www.itheima
 * @Date: 2019/7/7 13:48
 * @Description: com.changgou.token
 *  使用公钥解密令牌数据
 ****/
public class ParseJwtTest {

    /***
     * 校验令牌
     */
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IlJPTEVfVklQLFJPTEVfVVNFUiIsIm5hbWUiOiJpdGhlaW1hIiwiaWQiOiIxIn0.hUkc7bbRWh9VqLDZh28HUPY1ft42EH0csloO10MV7j_7Jy4UVelvNgl84lhPlrGLymNNMGKGh_7SmOCBqbIKNWVelxibQWY_Ymrwt8SMD-bW5jVBIIBied9K2_pZZZEpxm92upBnheQXUg3ghW04vsBWWbMGXYWreVz8VpYqcDVGMP3Nh1fQ1N2oTW_iQaMXjvO0-JV3ANS0m5rHx78h5aWwsCciNLvVNikjkh9z_1Y5-U0vC2DBLoc-XfSrJ73szIv1orRVnDSKMVlOdN3k1Xu03fK60FZ6f-4ztmk0A8Lt6D6i3hKnYuWAyNVXc2QLi-MYQ-84p0flr8KXOhwTPA";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtrB7o3WgkMhQ8W3x9IIwYsqhgRZCHXZ6z7WkX1Y7u9KjWfb3+GkaXDxSPEKskBTzgD3tXVgugaohh3yK5v2zvXeTffaSqMoFsb+FP8GuaYSezJlOcp6OlbzYjCQsXhlxHXvlzGBVsTdMBgh7fLUc159pG1aXFilVqQ2xeu8UYK+T4jlCZam1BsX5ngqtCYJRF29AN7uXrI/AYmZga/F4H+nN2mXrqeGHeIof0tQKNuQHON+He1ZYwnd24jOW71u1pLQ9GdUr+6aOo7BedXWCrpUkk8pb8HG9Tx/7luzPVYggh31WTKfaIvllFBpcfeXBj7fu/RC4ZTU5WHQ9OqStLQIDAQAB-----END PUBLIC KEY-----";

        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容 载荷
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
