package com.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.security.KeyStore.PasswordProtection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
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

@WebServlet(name = "GetPass", urlPatterns = {"/GetPass"})
public class GetPass extends HttpServlet {

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
            //String hostname = InetAddress.getLocalHost().getHostName();
            String pcHostName = InetAddress.getLocalHost().getHostName();
            String ApplicationName = ((HttpServletRequest) request).getContextPath().replace("/", "");
            String ApplicationName1 = request.getContextPath();
            //String ip=request.getLocalAddr();
            //System.out.println("ip: "+ip);
            System.out.println("ApplicationName1: " + ApplicationName1);
            String serverHostName = request.getServerName();
            System.out.println("host: " + serverHostName);
            int port = request.getLocalPort();
            System.out.println("port: " + port);
            String Pid = request.getParameter("pid");
            String email = request.getParameter("email");
            con = DB.getConnection();
            ps = con.prepareStatement("select Pass from table_patient where Pid=? and Email=? ");
            ps.setString(1, Pid);
            ps.setString(2, email);
            rs = ps.executeQuery();
            if (!rs.next()) {
                String msg = "Sorry! Patient ID or Patient Email does not match.";
                HttpSession sessions = request.getSession();
                sessions.setAttribute("msg", msg);
                response.sendRedirect("ForgotPass.jsp");
            } else {
                String pwd = rs.getString("Pass");
                final String username = "";//your email
                final String password = "";//your password
                Properties props = new Properties();
                props.put("mail.smtp.auth", true);
                props.put("mail.smtp.starttls.enable", true);
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                HttpSession session = Session.getInstance(props, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordProtection getPasswordAuthentication() {
                        return new PasswordProtection(username, password);
                    }
                });
                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                    MimeBodyPart textPart = new MimeBodyPart();
                    MimeBodyPart htmlPart = new MimeBodyPart();
                    Multipart multipart = new MimeMultipart();
                    String final_Text = "Your Password is:   " + pwd;
                    textPart.setText(final_Text);
                    htmlPart.setContent("<a href='http://" + serverHostName + ":" + port + "/" + ApplicationName + "/PatientLogin.jsp'>Login Now</a><br>", "text/html");
                    multipart.addBodyPart(htmlPart);
                    message.setSubject("Regarding Patient Password");
                    multipart.addBodyPart(textPart);
                    message.setContent(multipart);
                    //out.println("Sending");
                    System.out.println("sending done..");
                    Transport.send(message);
                    HttpSession sessions = request.getSession();
                    String msg1 = "Your Password is sent to your email. &nbsp; <a style='text-decoration:underline;' href='mailto:" + pwd + "'>Click Here</a>&nbsp; to check it.";
                    sessions.setAttribute("msg1", msg1);
                    response.sendRedirect("ForgotPass.jsp");
                } catch (MessagingException | IOException e) {
                    out.println(e);
                }
            }

        } catch (IOException | SQLException e) {
            out.println(e);
        }
    }
}
