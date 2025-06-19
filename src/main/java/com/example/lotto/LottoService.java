package com.example.lotto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

@Service
public class LottoService {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPass;

    public List<Integer> generate() {
        Random rand = new Random();

        while (true) {
            // 1. 로또 번호 생성
            Set<Integer> lotto = new TreeSet<>();
            while (lotto.size() < 6) {
                lotto.add(rand.nextInt(45) + 1); // 1~45
            }

            List<Integer> list = new ArrayList<>(lotto);
            String result = String.join("/", list.stream().map(String::valueOf).toArray(String[]::new));

            // 2. 중복 검사
            if (isDuplicate(result)) {
                continue; // 중복이면 다시 뽑기
            }

            // 3. 3연속 번호 검사
            boolean hasLinear = false;
            for (int i = 0; i < 4; i++) {
                if (list.get(i) + 1 == list.get(i + 1) &&
                        list.get(i) + 2 == list.get(i + 2)) {
                    hasLinear = true;
                    break;
                }
            }
            if (hasLinear) {
                continue; // 3연속이면 다시 뽑기
            }

            // 4. 통과했으면 리턴
            return list;
        }
    }

    private boolean isDuplicate(String numbers) {
        String sql = "SELECT COUNT(*) FROM lotto_history WHERE numbers = ?";
        try (
                Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, numbers);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            // DB 오류도 재시도 처리
            return true;
        }
        return false;
    }
}