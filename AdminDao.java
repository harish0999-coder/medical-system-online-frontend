package com.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "AdminDao", urlPatterns = { "/AdminDao" })
public class AdminDao extends HttpServlet {

	PrintWriter out;
	Connection con;
	PreparedStatement ps;
	ResultSet rs;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		out = response.getWriter();
		try {
			con = DB.getConnection();
			Random random = new Random();
			int newRandomCaptcha = random.nextInt(9000) + 10000;
			String email = request.getParameter("email");
			String Pass = request.getParameter("password");
			String vercode = request.getParameter("vercode");
			String captchaDB = null;

			ResultSet captchResultSet = DB.getResultFromSqlQuery("select * from tblcaptcha");
			if (captchResultSet.next()) {
				captchaDB = captchResultSet.getString(1);
			}
			if (captchaDB.equals(vercode)) {
				String sql = "select * from admin where email=? and password=?";
				ps = con.prepareStatement(sql);
				ps.setString(1, email);
				ps.setString(2, Pass);
				ps.executeQuery();
				rs = ps.executeQuery();
				if (rs.next()) {
					HttpSession session = request.getSession();
					session.setAttribute("name", email);
					int update = DB.insertUpdateFromSqlQuery("update tblcaptcha set captcha='" + newRandomCaptcha + "'");
					response.sendRedirect("AdminHome.jsp");
				} else {
					String msg = "Invalid Username or Password...";
					HttpSession session = request.getSession();
					session.setAttribute("msg", msg);
					int update = DB.insertUpdateFromSqlQuery("update tblcaptcha set captcha='" + newRandomCaptcha + "'");
					response.sendRedirect("AdminForm.jsp");
				}
			} else {
				HttpSession session = request.getSession();
				String message = "You have enter invalid verification code";
				session.setAttribute("verificationCode", message);
				int update = DB.insertUpdateFromSqlQuery("update tblcaptcha set captcha='" + newRandomCaptcha + "'");
				response.sendRedirect("AdminForm.jsp");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}