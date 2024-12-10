//package org.swu.filter;
//
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class RateLimitFilter extends OncePerRequestFilter {
//
//    private static final int MAX_REQUESTS_PER_MINUTE = 100; // 每分钟最大请求数
//    private final ConcurrentHashMap<String, Integer> requestCounts = new ConcurrentHashMap<>();
//    private final ConcurrentHashMap<String, Long> requestTimestamps = new ConcurrentHashMap<>();
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        String clientIp = request.getRemoteAddr();
//        long currentTime = System.currentTimeMillis();
//
//        // 清理过期的计数
//        requestCounts.compute(clientIp, (key, count) -> {
//            Long lastTimestamp = requestTimestamps.getOrDefault(key, 0L);
//            if (currentTime - lastTimestamp > 60 * 1000) { // 超过一分钟重置计数
//                requestTimestamps.put(key, currentTime);
//                return 1;
//            }
//            return (count == null ? 1 : count + 1);
//        });
//
//        // 检查是否超过限流
//        if (requestCounts.get(clientIp) > MAX_REQUESTS_PER_MINUTE) {
//            response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
//            response.getWriter().write("Too many requests - Rate limit exceeded");
//            return; // 不再继续处理请求
//        }
//
//        // 如果未超限，继续处理请求
//        filterChain.doFilter(request, response);
//    }
//}
//
