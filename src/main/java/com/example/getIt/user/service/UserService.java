package com.example.getIt.user.service;

import com.example.getIt.user.jwt.entity.RefreshTokenEntity;
import com.example.getIt.user.jwt.repository.RefreshTokenRepository;
import com.example.getIt.user.jwt.DTO.TokenDTO;
import com.example.getIt.user.jwt.TokenProvider;
import com.example.getIt.product.DTO.ProductDTO;
import com.example.getIt.user.DTO.UserDTO;
import com.example.getIt.product.entity.ProductEntity;
import com.example.getIt.user.entity.UserEntity;
import com.example.getIt.product.entity.UserProductEntity;
import com.example.getIt.product.repository.ProductRepository;
import com.example.getIt.product.repository.UserProductRepository;
import com.example.getIt.user.repository.UserRepository;
import com.example.getIt.util.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.getIt.util.ValidationRegex.isRegexEmail;
import static com.example.getIt.util.ValidationRegex.isRegexPwd;

@Service
public class UserService {
    private UserRepository userRepository;
    private UserProductRepository userProductRepository;
    private ProductRepository productRepository;
    private PasswordEncoder passwordEncoder;
    private TokenProvider tokenProvider;
    private RefreshTokenRepository refreshTokenRepository;
    private AuthenticationManagerBuilder authenticationManagerBuilder;



    public UserService(UserRepository userRepository, UserProductRepository userProductRepository,
                       ProductRepository productRepository, PasswordEncoder passwordEncoder,
                       TokenProvider tokenProvider, RefreshTokenRepository refreshTokenRepository,
                       AuthenticationManagerBuilder authenticationManagerBuilder){
        this.userRepository = userRepository;
        this.userProductRepository = userProductRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    public TokenDTO signIn(UserDTO.User user) throws BaseException {
        if(user.getEmail() == null || user.getNickName() == null || user.getPassword() == null){
             throw new BaseException(BaseResponseStatus.POST_USERS_EMPTY);
        }
        if(!isRegexEmail(user.getEmail())){
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
        }
        if(!isRegexPwd(user.getPassword())){
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_PWD);
        }
        if(isHaveEmail(user.getEmail())){
            throw new BaseException(BaseResponseStatus.DUPLICATE_EMAIL);
        }
        if(isHaveNickName(user.getNickName())){
            throw new BaseException(BaseResponseStatus.DUPLICATE_NICKNAME);
        }
        String password = user.getPassword();
        try{
            String encodedPwd = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPwd);
        }catch (Exception e){
            throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
        }
        UserEntity userEntity = UserEntity.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .nickName(user.getNickName())
                .birthday(user.getBirthday())
                .role(Role.ROLE_USER)
                .provider("Not_Social")
                .build();
        user.setPassword(password);
        userRepository.save(userEntity);
        return token(user);

    }

    public boolean isHaveNickName(String nickName) {
        return this.userRepository.existsByNickname(nickName);
    }

    public boolean isHaveEmail(String email) { return this.userRepository.existsByEmail(email); }


    public TokenDTO token(UserDTO.User user){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        // 2. ????????? ?????? (????????? ???????????? ??????) ??? ??????????????? ??????
        //    authenticate ???????????? ????????? ??? ??? CustomUserDetailsService ?????? ???????????? loadUserByUsername ???????????? ?????????
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // 3. ?????? ????????? ???????????? JWT ?????? ??????
        TokenDTO tokenDto = tokenProvider.generateTokenDto(authentication);
        // 4. RefreshToken ??????
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        // 5. ?????? ??????
        return tokenDto;
    }



    public TokenDTO logIn(UserDTO.User user) throws BaseException{
        if(!isRegexEmail(user.getEmail())){
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
        }

        Optional<UserEntity> optional = userRepository.findByEmail(user.getEmail());
        if(optional.isEmpty()){
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }else{
            UserEntity userEntity = optional.get();
            if(!userEntity.getProvider().equals("Not_Social")){
                throw new BaseException(BaseResponseStatus.SOCIAL);
            }
            if(passwordEncoder.matches(user.getPassword(), userEntity.getPassword())) { // ?????? ????????? password??? ????????? ????????? ??????????????? ?????????.
                return token(user);
            }else{
                throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_PASSWORD);
            }

        }
    }


    public UserDTO.UserProtected getUser(Principal principal) throws BaseException {
        try{
            UserDTO.UserLikeList userLikeList = this.getUserLikeList(principal);
            UserEntity userEntity = userRepository.findByEmail(principal.getName()).get();

            return new UserDTO.UserProtected(
                    userEntity.getUserIdx(),
                    userEntity.getEmail(),
                    userEntity.getNickname(),
                    userEntity.getBirthday(),
                    userEntity.getJob(),
                    userEntity.getStatus(),
                    userEntity.getRole(),
                    userLikeList.getLikeProduct()
            );
        }catch (Exception e){
            System.out.println("Error: "+e);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

    }

    public TokenDTO reissue(TokenDTO tokenRequestDto) { //?????????
        // 1. Refresh Token ??????
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token ??? ???????????? ????????????.");
        }

        // 2. Access Token ?????? Member ID ????????????
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        // 3. ??????????????? Member ID ??? ???????????? Refresh Token ??? ?????????
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByKeyId(authentication.getName())
                .orElseThrow(() -> new RuntimeException("???????????? ??? ??????????????????."));

        // 4. Refresh Token ??????????????? ??????
        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("????????? ?????? ????????? ???????????? ????????????.");
        }

        // 5. ????????? ?????? ??????
        TokenDTO tokenDto = tokenProvider.generateTokenDto(authentication);

        // 6. ????????? ?????? ????????????
        RefreshTokenEntity newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // ?????? ??????
        return tokenDto;
    }

    public void patchPwd(Principal principal, UserDTO.UserPwd user) throws BaseException{
        Optional<UserEntity> optional = this.userRepository.findByEmail(principal.getName());
        if(optional.isEmpty()){
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        if(user.getPassword() == null || user.getNewPassword() == null){
            throw new BaseException(BaseResponseStatus.POST_USERS_EMPTY);
        }
        UserEntity userEntity = optional.get();
        if(!passwordEncoder.matches(user.getPassword(), userEntity.getPassword())) { // ?????? ????????? password??? ????????? ????????? ??????????????? ?????????.
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_PASSWORD);
        }
        if(user.getPassword().equals(user.getNewPassword())){
            throw new BaseException(BaseResponseStatus.PASSWORD_EQUALS_NEWPASSWORD);
        }

        if(!userEntity.getProvider().equals("Not_Social")){
            throw new BaseException(BaseResponseStatus.SOCIAL);
        }
        if(!isRegexPwd(user.getNewPassword())){
            throw new BaseException(BaseResponseStatus.POST_USERS_INVALID_PWD);
        }
        String encodedPwd;
        try{
            encodedPwd = passwordEncoder.encode(user.getNewPassword());
        }catch (Exception e){
            throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
        }
        userEntity.changePwd(encodedPwd);
        userRepository.save(userEntity);
    }

    public UserDTO.UserLikeList getUserLikeList(Principal principal) throws BaseException{
        try{
            UserEntity userEntity = userRepository.findByEmail(principal.getName()).get();
            List<UserProductEntity> products = userProductRepository.findAllByUserIdx(userEntity);
            List<ProductDTO.GetProduct> likeProduct = new ArrayList<>();

            for(UserProductEntity temp : products){
                ProductEntity likeProductInfo = productRepository.findAllByProductIdx(temp.getProductIdx().getProductIdx());
                likeProduct.add(new ProductDTO.GetProduct(
                        likeProductInfo.getProductIdx(),
                        likeProductInfo.getName(),
                        likeProductInfo.getBrand(),
                        likeProductInfo.getType(),
                        likeProductInfo.getImage(),
                        likeProductInfo.getLowestprice(),
                        likeProductInfo.getProductId(),
                        likeProductInfo.getProductUrl()
                ));
            }

            return new UserDTO.UserLikeList(
                    userEntity.getUserIdx(),
                    likeProduct
            );
        }catch (Exception e){
            System.out.println("Error: "+e);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
