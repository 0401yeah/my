package com.parking.controller;

import com.parking.common.Result;
import com.parking.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ExpenseService expenseService;

    @RequestMapping("/kpi")
    public Result<Map<String, Object>> getKPI() {
        try {
            Map<String, Object> data = new HashMap<>();
            
            String totalIncomeSql = "SELECT COALESCE(SUM(pay_amount), 0) FROM app_order WHERE status = 1";
            BigDecimal totalIncome = jdbcTemplate.queryForObject(totalIncomeSql, BigDecimal.class);
            
            String parkingIncomeSql = "SELECT COALESCE(SUM(pay_amount), 0) FROM app_parking_record WHERE payment_status = 1";
            BigDecimal parkingIncome = jdbcTemplate.queryForObject(parkingIncomeSql, BigDecimal.class);
            totalIncome = totalIncome.add(parkingIncome);
            
            String totalExpenseSql = "SELECT COALESCE(SUM(amount), 0) FROM app_expense WHERE status = 1";
            BigDecimal totalExpense = jdbcTemplate.queryForObject(totalExpenseSql, BigDecimal.class);
            
            BigDecimal netProfit = totalIncome.subtract(totalExpense);
            
            String lastMonthIncomeSql = "SELECT COALESCE(SUM(pay_amount), 0) FROM app_order WHERE status = 1 AND gmt_create < DATE_SUB(DATE_FORMAT(NOW(), '%Y-%m-01'), INTERVAL 1 MONTH)";
            BigDecimal lastMonthIncome = jdbcTemplate.queryForObject(lastMonthIncomeSql, BigDecimal.class);
            
            BigDecimal growthRate = BigDecimal.ZERO;
            if (lastMonthIncome != null && lastMonthIncome.compareTo(BigDecimal.ZERO) > 0) {
                growthRate = totalIncome.subtract(lastMonthIncome)
                    .divide(lastMonthIncome, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            }
            
            data.put("totalIncome", totalIncome);
            data.put("totalExpense", totalExpense);
            data.put("netProfit", netProfit);
            data.put("growthRate", growthRate);
            
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取KPI数据失败: " + e.getMessage());
        }
    }

    @RequestMapping("/composition")
    public Result<Map<String, Object>> getComposition() {
        try {
            Map<String, Object> data = new HashMap<>();
            
            Map<String, Object> income = new HashMap<>();
            income.put("labels", Arrays.asList("临时停车费", "月租费", "代泊服务", "洗车服务", "其他收入"));
            
            String parkingFeeSql = "SELECT COALESCE(SUM(pay_amount), 0) FROM app_parking_record WHERE payment_status = 1";
            BigDecimal parkingFee = jdbcTemplate.queryForObject(parkingFeeSql, BigDecimal.class);
            
            String valetSql = "SELECT COALESCE(SUM(pay_amount), 0) FROM app_order WHERE order_type = 1 AND status = 1";
            BigDecimal valetFee = jdbcTemplate.queryForObject(valetSql, BigDecimal.class);
            
            String washSql = "SELECT COALESCE(SUM(pay_amount), 0) FROM app_order WHERE order_type = 2 AND status = 1";
            BigDecimal washFee = jdbcTemplate.queryForObject(washSql, BigDecimal.class);
            
            String monthlyFeeSql = "SELECT COALESCE(SUM(pay_amount), 0) FROM app_order WHERE order_type = 3 AND status = 1";
            BigDecimal monthlyFee = jdbcTemplate.queryForObject(monthlyFeeSql, BigDecimal.class);
            
            String otherIncomeSql = "SELECT COALESCE(SUM(pay_amount), 0) FROM app_order WHERE order_type NOT IN (1, 2, 3) AND status = 1";
            BigDecimal otherIncome = jdbcTemplate.queryForObject(otherIncomeSql, BigDecimal.class);
            
            income.put("data", Arrays.asList(
                parkingFee != null ? parkingFee : BigDecimal.ZERO,
                monthlyFee != null ? monthlyFee : BigDecimal.ZERO,
                valetFee != null ? valetFee : BigDecimal.ZERO,
                washFee != null ? washFee : BigDecimal.ZERO,
                otherIncome != null ? otherIncome : BigDecimal.ZERO
            ));
            data.put("income", income);
            
            Map<String, Object> expense = new HashMap<>();
            expense.put("labels", Arrays.asList("设备维护费", "电费", "人工成本", "退款金额", "其他支出"));
            
            List<BigDecimal> expenseData = new ArrayList<>();
            String[] categories = {"设备维护费", "电费", "人工成本", "退款金额", "其他支出"};
            for (String category : categories) {
                String sql = "SELECT COALESCE(SUM(amount), 0) FROM app_expense WHERE category = ? AND status = 1";
                BigDecimal amount = jdbcTemplate.queryForObject(sql, BigDecimal.class, category);
                expenseData.add(amount != null ? amount : BigDecimal.ZERO);
            }
            expense.put("data", expenseData);
            data.put("expense", expense);
            
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取构成数据失败: " + e.getMessage());
        }
    }

    @RequestMapping("/trend")
    public Result<Map<String, Object>> getTrend(@RequestParam(required = false, defaultValue = "week") String period) {
        try {
            Map<String, Object> data = new HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
            SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            
            int days = "month".equals(period) ? 30 : 7;
            List<String> dates = new ArrayList<>();
            List<BigDecimal> incomeList = new ArrayList<>();
            List<BigDecimal> expenseList = new ArrayList<>();
            
            for (int i = days - 1; i >= 0; i--) {
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_MONTH, -i);
                dates.add(sdf.format(cal.getTime()));
                
                String dateStr = fullSdf.format(cal.getTime());
                
                String orderIncomeSql = "SELECT COALESCE(SUM(pay_amount), 0) FROM app_order WHERE DATE(gmt_create) = '" + dateStr + "' AND status = 1";
                BigDecimal orderIncome = jdbcTemplate.queryForObject(orderIncomeSql, BigDecimal.class);
                
                String parkingIncomeSql = "SELECT COALESCE(SUM(pay_amount), 0) FROM app_parking_record WHERE DATE(gmt_out) = '" + dateStr + "' AND payment_status = 1";
                BigDecimal parkingIncome = jdbcTemplate.queryForObject(parkingIncomeSql, BigDecimal.class);
                
                BigDecimal dayIncome = (orderIncome != null ? orderIncome : BigDecimal.ZERO)
                    .add(parkingIncome != null ? parkingIncome : BigDecimal.ZERO);
                incomeList.add(dayIncome);
                
                String expenseSql = "SELECT COALESCE(SUM(amount), 0) FROM app_expense WHERE DATE(gmt_create) = '" + dateStr + "' AND status = 1";
                BigDecimal dayExpense = jdbcTemplate.queryForObject(expenseSql, BigDecimal.class);
                expenseList.add(dayExpense != null ? dayExpense : BigDecimal.ZERO);
            }
            
            data.put("dates", dates);
            data.put("income", incomeList);
            data.put("expense", expenseList);
            
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取趋势数据失败: " + e.getMessage());
        }
    }

    @RequestMapping("/table")
    public Result<Map<String, Object>> getTable() {
        try {
            Map<String, Object> data = new HashMap<>();
            
            String parkingFeeSql = "SELECT COALESCE(SUM(pay_amount), 0) as amount, COUNT(*) as count FROM app_parking_record WHERE payment_status = 1";
            Map<String, Object> parkingResult = jdbcTemplate.queryForMap(parkingFeeSql);
            BigDecimal parkingFee = (BigDecimal) parkingResult.get("amount");
            Long parkingCount = ((Number) parkingResult.get("count")).longValue();
            
            String valetSql = "SELECT COALESCE(SUM(pay_amount), 0) as amount, COUNT(*) as count FROM app_order WHERE order_type = 1 AND status = 1";
            Map<String, Object> valetResult = jdbcTemplate.queryForMap(valetSql);
            BigDecimal valetFee = (BigDecimal) valetResult.get("amount");
            Long valetCount = ((Number) valetResult.get("count")).longValue();
            
            String washSql = "SELECT COALESCE(SUM(pay_amount), 0) as amount, COUNT(*) as count FROM app_order WHERE order_type = 2 AND status = 1";
            Map<String, Object> washResult = jdbcTemplate.queryForMap(washSql);
            BigDecimal washFee = (BigDecimal) washResult.get("amount");
            Long washCount = ((Number) washResult.get("count")).longValue();
            
            String monthlySql = "SELECT COALESCE(SUM(pay_amount), 0) as amount, COUNT(*) as count FROM app_order WHERE order_type = 3 AND status = 1";
            Map<String, Object> monthlyResult = jdbcTemplate.queryForMap(monthlySql);
            BigDecimal monthlyFee = (BigDecimal) monthlyResult.get("amount");
            Long monthlyCount = ((Number) monthlyResult.get("count")).longValue();
            
            String otherSql = "SELECT COALESCE(SUM(pay_amount), 0) as amount, COUNT(*) as count FROM app_order WHERE order_type NOT IN (1, 2, 3) AND status = 1";
            Map<String, Object> otherResult = jdbcTemplate.queryForMap(otherSql);
            BigDecimal otherFee = (BigDecimal) otherResult.get("amount");
            Long otherCount = ((Number) otherResult.get("count")).longValue();
            
            List<Map<String, Object>> incomeList = new ArrayList<>();
            incomeList.add(createItem("临时停车费", parkingFee != null ? parkingFee : BigDecimal.ZERO, parkingCount != null ? parkingCount : 0L));
            incomeList.add(createItem("月租费", monthlyFee != null ? monthlyFee : BigDecimal.ZERO, monthlyCount != null ? monthlyCount : 0L));
            incomeList.add(createItem("代泊服务", valetFee != null ? valetFee : BigDecimal.ZERO, valetCount != null ? valetCount : 0L));
            incomeList.add(createItem("洗车服务", washFee != null ? washFee : BigDecimal.ZERO, washCount != null ? washCount : 0L));
            incomeList.add(createItem("其他收入", otherFee != null ? otherFee : BigDecimal.ZERO, otherCount != null ? otherCount : 0L));
            data.put("income", incomeList);
            
            List<Map<String, Object>> expenseList = new ArrayList<>();
            String[] categories = {"设备维护费", "电费", "人工成本", "退款金额", "其他支出"};
            for (String category : categories) {
                String sql = "SELECT COALESCE(SUM(amount), 0) as amount, COUNT(*) as count FROM app_expense WHERE category = ? AND status = 1";
                Map<String, Object> result = jdbcTemplate.queryForMap(sql, category);
                BigDecimal amount = (BigDecimal) result.get("amount");
                Long count = ((Number) result.get("count")).longValue();
                expenseList.add(createItem(category, amount != null ? amount : BigDecimal.ZERO, count != null ? count : 0L));
            }
            data.put("expense", expenseList);
            
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取表格数据失败: " + e.getMessage());
        }
    }

    @RequestMapping("/parking-lot-revenue")
    public Result<Map<String, Object>> getParkingLotRevenue() {
        try {
            Map<String, Object> data = new HashMap<>();
            
            String sql = "SELECT pl.name, " +
                        "COALESCE(SUM(o.pay_amount), 0) + COALESCE((SELECT SUM(pr.pay_amount) FROM app_parking_record pr WHERE pr.parking_lot_id = pl.id AND pr.payment_status = 1), 0) as revenue " +
                        "FROM app_parking_lot pl " +
                        "LEFT JOIN app_order o ON pl.id = o.parking_lot_id AND o.status =1 " +
                        "GROUP BY pl.id, pl.name " +
                        "ORDER BY revenue DESC";
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
            
            List<String> labels = new ArrayList<>();
            List<BigDecimal> revenueData = new ArrayList<>();
            
            for (Map<String, Object> row : results) {
                labels.add((String) row.get("name"));
                revenueData.add((BigDecimal) row.get("revenue"));
            }
            
            data.put("labels", labels);
            data.put("data", revenueData);
            
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取停车场收入数据失败: " + e.getMessage());
        }
    }
    
    private Map<String, Object> createItem(String category, BigDecimal amount, Long count) {
        Map<String, Object> item = new HashMap<>();
        item.put("category", category);
        item.put("amount", amount);
        item.put("count", count);
        return item;
    }
}
