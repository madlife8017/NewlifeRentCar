package com.java.car.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.java.car.domain.Car;
import com.java.car.domain.CarGrade;
import com.java.car.domain.CarSize;
import com.java.common.CarCondition;
import com.java.common.DataBaseConnection;
import com.java.user.domain.User;
import com.java.user.domain.UserGrade;

public class CarRepository {

	private DataBaseConnection connection = DataBaseConnection.getInstance();
	private Car car;
	private User user;

	// 차량 정보 DB 추가 메서드
	public void addCar(Car Car) {
		String sql = "INSERT INTO cars "
				+ "(car_num, car_id, car_model, car_size, car_fee, car_grade) "
				+ "VALUES(cars_seq.NEXTVAL, ?, ?, ?, ?, ?)";

		try(Connection conn = connection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, Car.getCarId());
			pstmt.setString(2, Car.getCarModel());
			pstmt.setString(3, Car.getCarSize().toString());
			pstmt.setInt(4, Car.getCarFee());
			pstmt.setString(5, Car.getCarGrade().toString());

			if(pstmt.executeUpdate() == 1) {
				System.out.println("차량 등록이 정상 처리되었습니다.");
			} else {
				System.out.println("차량 등록에 실패했습니다. 관리자에게 문의하세요.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 차량 정보 DB 검색 메서드
	public List<Car> searchCarList(CarCondition condition, String keyword) {

		String sql = "";
		List<Car> carList = new ArrayList<>();

		switch (condition) {
		case CAR_MODEL: case CAR_ID: 
			sql = "SELECT * FROM cars WHERE " + condition.toString()
			+ " LIKE " + keyword;
			break;
		case ONRENT: case AVAILABLE:
			sql = "SELECT * FROM cars WHERE CAR_STATUS = '" + condition.toString() + "'";
			break;
		case ALL:
			sql = "SELECT * FROM cars";
		}

		try(Connection conn = connection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery();) {
			while(rs.next()) {
				CarSize carSize = CarSize.valueOf(rs.getString("car_size"));
				CarGrade carGrade = CarGrade.valueOf(rs.getString("car_grade"));
				Car car = new Car(
						rs.getInt("car_num"),
						rs.getString("car_id"),
						rs.getString("car_model"),
						carSize,
						rs.getInt("car_fee"),
						carGrade,
						rs.getString("car_status"),
						rs.getInt("user_num")
						);
				carList.add(car);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return carList;

	}

	// 차량 번호를 통해 DB의 차량 정보를 수정하는 메서드
	public void modifyCarDB(int inputCarNum, CarCondition condition, String keyword) {

		String sql = "UPDATE cars SET " + condition.toString()
		+ " = " + keyword + " WHERE car_num = " + inputCarNum;

		try(Connection conn = connection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);) {
			int result = pstmt.executeUpdate(sql);

			if(result == 1) {
				System.out.println("차량 정보 수정 완료!");
			} else {
				System.out.println("차량 정보 수정 실패.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	// 차량 번호를 통해 DB의 차량 정보를 삭제하는 메서드
	public void deleteCarDB(int inputCarNum) {

		String sql = "DELETE FROM cars WHERE car_num = " + inputCarNum;

		try(Connection conn = connection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);) {
			int result = pstmt.executeUpdate(sql);

			if(result == 1) {
				System.out.println("차량 정보 삭제 완료!");
			} else {
				System.out.println("차량 정보 삭제 실패.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 차량 번호를 통해 차량 정보와 함께 사용자 정보를 검색하는 메서드
	public void rentUserSearchDB(int searchNum) {

		String sql = "SELECT * FROM "
				+ "(SELECT c.car_num, c.car_Id, c.car_model, c.car_size, c.car_fee, c.car_grade, c.car_status, "
				+ "c.user_num, r.user_name, r.phone_number, r.user_age, r.user_grade, r.user_location "
				+ "FROM cars c "
				+ "JOIN rent_users r "
				+ "ON c.user_num = r.user_num) WHERE car_num = " + searchNum;

		try(Connection conn = connection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery();) {
			while(rs.next()) {
				CarSize carSize = CarSize.valueOf(rs.getString("car_size"));
				CarGrade carGrade = CarGrade.valueOf(rs.getString("car_grade"));
				Car car = new Car(
						rs.getInt("car_num"),
						rs.getString("car_id"),
						rs.getString("car_model"),
						carSize,
						rs.getInt("car_fee"),
						carGrade,
						rs.getString("car_status"),
						rs.getInt("user_num")
						);
				UserGrade grade = UserGrade.valueOf(rs.getString("user_grade"));
				User user = new User(rs.getInt("user_num"),
						rs.getString("user_name"),
						rs.getString("phone_number"),
						rs.getInt("user_age"),
						grade,
						rs.getString("user_location")
						);
				showSearchResultAll(car, user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	private void showSearchResultAll(Car car, User user) {
		
		System.out.println("이용중인 차량 정보 \n" + car);
		System.out.println("이용중인 사용자 정보 \n" + user);
		
		return;
	}
	
}
