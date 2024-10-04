package com.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ContactDao", urlPatterns = { "/ContactDao" })
public class ContactDao extends HttpServlet {

	PrintWriter out;
	Connection con;
	PreparedStatement ps;
	ResultSet rs;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		try {
			out = response.getWriter();
			String name = request.getParameter("uname");
			String email = request.getParameter("email");
			String contact = request.getParameter("phone");
			String subject = request.getParameter("subject");
			String comment = request.getParameter("message");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(new Date());
			HttpSession session = request.getSession();
			con = DB.getConnection();
			ps = con.prepareStatement(
					"insert into contact(name,email,contact,subject,message,date) values (?,?,?,?,?,?)");
			ps.setString(1, name);
			ps.setString(2, email);
			ps.setString(3, contact);
			ps.setString(4, subject);
			ps.setString(5, comment);
			ps.setString(6, date);
			int i = ps.executeUpdate();
			if (i > 0) {
				session.setAttribute("msg", "Thank you <b>" + name + "</b>, your message has been submitted to us.");
				response.sendRedirect("Contact.jsp");
			}

		} catch (Exception e) {
			out.println(e);
		}
	}

}
