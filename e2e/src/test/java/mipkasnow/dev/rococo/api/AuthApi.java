package mipkasnow.dev.rococo.api;

import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface AuthApi {

    @GET("oauth2/authorize")
    @Nonnull
    Call<Void> authorize(
            @Query("response_type") String responseType,
            @Query("client_id") String clientId,
            @Query("scope") String scope,
            @Query(value = "redirect_uri", encoded = true) String redirectUri,
            @Query("code_challenge") String codeChallenge,
            @Query("code_challenge_method") String codeChallengeMethod
    );

    @POST("oauth2/token")
    @FormUrlEncoded
    @Nonnull
    Call<JsonNode> token(
            @Field("client_id") String clientId,
            @Field(value = "redirect_uri", encoded = true) String redirectUri,
            @Field("grant_type") String grantType,
            @Field("code") String code,
            @Field("code_verifier") String codeVerifier
    );

    @GET("login")
    @Nonnull
    Call<ResponseBody> requestLoginForm();

    @GET
    @Nonnull
    Call<ResponseBody> follow(@Url String url);

    @POST("oauth2/authorize")
    @FormUrlEncoded
    @Nonnull
    Call<Void> consent(
            @Field("client_id") String clientId,
            @Field("state") String state,
            @Field("scope") String scope
    );

    @POST("login")
    @FormUrlEncoded
    @Nonnull
    Call<Void> login(
            @Field("username") String username,
            @Field("password") String password,
            @Field("_csrf") String csrf
    );

    @GET("register")
    @Nonnull
    Call<ResponseBody> requestRegisterForm();

    @POST("register")
    @FormUrlEncoded
    @Nonnull
    Call<Void> register(
            @Field("username") String username,
            @Field("password") String password,
            @Field("passwordSubmit") String passwordSubmit,
            @Field("_csrf") String csrf
    );
}
