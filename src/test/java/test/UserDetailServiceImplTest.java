package test;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


class UserDetailServiceImplTest {


    public static void main(String[] args) {
        String encode = new BCryptPasswordEncoder().encode("123123");
        System.out.println(encode);
    }
}
