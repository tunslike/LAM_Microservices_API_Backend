package com.lamsuite.authservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lamsuite.authservice.model.ZohoMailBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerEntryService.class);

    private static final String notification_api_key = "6A6C0FB5B774E14CB03683C6F181F8A47981D3EF876F82D47A45A74ECB2190440B8387B5477B72FAD627E2E8BFCD2DB4";

    private static final String Zoho_API_Key = "Zoho-enczapikey wSsVR612/hbyWKt8mTT4crpuzVVUVgj3Fkp1igf06XapGfHK9Mc7k0WbAwGlFaUaFTJtFTEbp7IqmEoDhjdfj9sonw5UCSiF9mqRe1U4J3x17qnvhDzIXWlYkxKKKYkOzgltmmdgG84h+g==";

    private static final String notification_sender_FromName = "Finserve Investment Limited";

    private static final String notification_sender_FromAddress = "noreply@finserveinvestment.com";

    private static final String notification_sender_sendBaseUrl = "https://api.elasticemail.com/v2/email/send?apikey=";

    public NotificationService () {

    }

    public void SendStaffLoanVerificationNotification(String EmailAddress, String staffPhoneNumber, String staffEmployer,
                                                      String staffFullName, String verificationLink,
                                                      String loanAmount, String loanTenor, String loanInterest,
                                                      String loanRepayment, String employerContactName) {

        String fromName = notification_sender_FromName;
        String fromAddress = notification_sender_FromAddress;
        String subject = "Staff Loan Request Verification";
        String APIKey = notification_api_key;
        String templateName = "Finserve_Loan_Verification";

        try{

            final String uri_template = notification_sender_sendBaseUrl + APIKey + "&subject=" + subject + "&msgFrom=" + fromAddress + "&msgFromName="
                    + fromName + "&template=" + templateName + "&msgTo=" + EmailAddress + "&merge_employerContactName=" + employerContactName +
                    "&merge_staffFullName=" + staffFullName + "&merge_staffPhoneNumber=" + staffPhoneNumber + "&merge_staffEmployer=" +
                    staffEmployer + "&merge_loanAmount=" + loanAmount + "&merge_loanTenor=" + loanTenor + "&merge_loanInterest=" +
                    loanInterest + "&merge_loanRepayment=" + loanRepayment + "&merge_verificationLink=" + verificationLink;

            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri_template, String.class);

        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
    // end of service

    // service to send notification
    public void SendTestEmail() {

        String fromName = notification_sender_FromName;
        String fromAddress = notification_sender_FromAddress;
        String subject = "New Account Registration";
        String APIKey = notification_api_key;
        String templateName = "Finserve_New_Registration";

        try{

            final String uri_template = notification_sender_sendBaseUrl + APIKey + "&subject=" + subject + "&msgFrom=" + fromAddress + "&msgFromName="
                    + fromName + "&template=" + templateName + "&msgTo=tunslike@gmail.com";

            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri_template, String.class);

        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }// end of service


    private String convertHTMLToString(String employerContactName, String staffFullname, String staffPhoneNumber,
                                       String staffEmployer, String loanAmount, String loanTenor, String loanInterest, String loanRepayment) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">\n" +
                "\n" +
                "    <head>\n" +
                "        <meta charset=\"utf-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width\">\n" +
                "        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "        <meta name=\"x-apple-disable-message-reformatting\">\n" +
                "        <meta name=\"format-detection\" content=\"telephone=no,address=no,email=no,date=no,url=no\">\n" +
                "\n" +
                "        <meta name=\"color-scheme\" content=\"light\">\n" +
                "        <meta name=\"supported-color-schemes\" content=\"light\">\n" +
                "\n" +
                "        \n" +
                "        <!--[if !mso]><!-->\n" +
                "          \n" +
                "          <link rel=\"preload\" as=\"style\" href=\"https://fonts.googleapis.com/css2?family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&display=swap\">\n" +
                "          <link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css2?family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&display=swap\">\n" +
                "\n" +
                "          <style type=\"text/css\">\n" +
                "          // TODO: fix me!\n" +
                "            @import url(https://fonts.googleapis.com/css2?family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&family=Open+Sans:ital,wght@0,400;0,700;1,400;1,700&display=swap);\n" +
                "        </style>\n" +
                "        \n" +
                "        <!--<![endif]-->\n" +
                "\n" +
                "        <!--[if mso]>\n" +
                "          <style>\n" +
                "              // TODO: fix me!\n" +
                "              * {\n" +
                "                  font-family: sans-serif !important;\n" +
                "              }\n" +
                "          </style>\n" +
                "        <![endif]-->\n" +
                "    \n" +
                "        \n" +
                "        <!-- NOTE: the title is processed in the backend during the campaign dispatch -->\n" +
                "        <title></title>\n" +
                "\n" +
                "        <!--[if gte mso 9]>\n" +
                "        <xml>\n" +
                "            <o:OfficeDocumentSettings>\n" +
                "                <o:AllowPNG/>\n" +
                "                <o:PixelsPerInch>96</o:PixelsPerInch>\n" +
                "            </o:OfficeDocumentSettings>\n" +
                "        </xml>\n" +
                "        <![endif]-->\n" +
                "        \n" +
                "    <style>\n" +
                "        :root {\n" +
                "            color-scheme: light;\n" +
                "            supported-color-schemes: light;\n" +
                "        }\n" +
                "\n" +
                "        html,\n" +
                "        body {\n" +
                "            margin: 0 auto !important;\n" +
                "            padding: 0 !important;\n" +
                "            height: 100% !important;\n" +
                "            width: 100% !important;\n" +
                "\n" +
                "            overflow-wrap: break-word;\n" +
                "            -ms-word-break: break-all;\n" +
                "            -ms-word-break: break-word;\n" +
                "            word-break: break-all;\n" +
                "            word-break: break-word;\n" +
                "        }\n" +
                "\n" +
                "\n" +
                "        \n" +
                "  direction: undefined;\n" +
                "  center,\n" +
                "  #body_table {\n" +
                "    \n" +
                "  }\n" +
                "\n" +
                "  ul, ol {\n" +
                "    padding: 0;\n" +
                "    margin-top: 0;\n" +
                "    margin-bottom: 0;\n" +
                "  }\n" +
                "\n" +
                "  li {\n" +
                "    margin-bottom: 0;\n" +
                "  }\n" +
                "\n" +
                "  \n" +
                "\n" +
                "  .list-block-list-outside-left li {\n" +
                "    margin-left: 20px !important;\n" +
                "  }\n" +
                "\n" +
                "  .list-block-list-outside-right li {\n" +
                "    margin-right: 20px !important;\n" +
                "  }\n" +
                "\n" +
                "  \n" +
                "    .paragraph {\n" +
                "      font-size: 15px;\n" +
                "      font-family: Open Sans, sans-serif;\n" +
                "      font-weight: normal;\n" +
                "      font-style: normal;\n" +
                "      text-align: start;\n" +
                "      line-height: 1;\n" +
                "      text-decoration: none;\n" +
                "      color: #5f5f5f;\n" +
                "      \n" +
                "    }\n" +
                "  \n" +
                "\n" +
                "    .heading1 {\n" +
                "      font-size: 32px;\n" +
                "      font-family: Open Sans, sans-serif;\n" +
                "      font-weight: normal;\n" +
                "      font-style: normal;\n" +
                "      text-align: start;\n" +
                "      line-height: 1;\n" +
                "      text-decoration: none;\n" +
                "      color: #000000;\n" +
                "      \n" +
                "    }\n" +
                "  \n" +
                "\n" +
                "    .heading2 {\n" +
                "      font-size: 26px;\n" +
                "      font-family: Open Sans, sans-serif;\n" +
                "      font-weight: normal;\n" +
                "      font-style: normal;\n" +
                "      text-align: start;\n" +
                "      line-height: 1;\n" +
                "      text-decoration: none;\n" +
                "      color: #000000;\n" +
                "      \n" +
                "    }\n" +
                "  \n" +
                "\n" +
                "    .heading3 {\n" +
                "      font-size: 19px;\n" +
                "      font-family: Open Sans, sans-serif;\n" +
                "      font-weight: normal;\n" +
                "      font-style: normal;\n" +
                "      text-align: start;\n" +
                "      line-height: 1;\n" +
                "      text-decoration: none;\n" +
                "      color: #000000;\n" +
                "      \n" +
                "    }\n" +
                "  \n" +
                "\n" +
                "    .list {\n" +
                "      font-size: 15px;\n" +
                "      font-family: Open Sans, sans-serif;\n" +
                "      font-weight: normal;\n" +
                "      font-style: normal;\n" +
                "      text-align: start;\n" +
                "      line-height: 1;\n" +
                "      text-decoration: none;\n" +
                "      color: #5f5f5f;\n" +
                "      \n" +
                "    }\n" +
                "  \n" +
                "\n" +
                "  p a, \n" +
                "  li a {\n" +
                "    \n" +
                "  display: inline-block;  \n" +
                "    color: #5457FF;\n" +
                "    text-decoration: none;\n" +
                "    font-style: normal;\n" +
                "    font-weight: normal;\n" +
                "\n" +
                "  }\n" +
                "\n" +
                "  .button-table a {\n" +
                "    text-decoration: none;\n" +
                "    font-style: normal;\n" +
                "    font-weight: normal;\n" +
                "  }\n" +
                "\n" +
                "  .paragraph > span {text-decoration: none;}.heading1 > span {text-decoration: none;}.heading2 > span {text-decoration: none;}.heading3 > span {text-decoration: none;}.list > span {text-decoration: none;}\n" +
                "\n" +
                "\n" +
                "        * {\n" +
                "            -ms-text-size-adjust: 100%;\n" +
                "            -webkit-text-size-adjust: 100%;\n" +
                "        }\n" +
                "\n" +
                "        div[style*=\"margin: 16px 0\"] {\n" +
                "            margin: 0 !important;\n" +
                "        }\n" +
                "\n" +
                "        #MessageViewBody,\n" +
                "        #MessageWebViewDiv {\n" +
                "            width: 100% !important;\n" +
                "        }\n" +
                "\n" +
                "        table {\n" +
                "            border-collapse: collapse;\n" +
                "            border-spacing: 0;\n" +
                "            mso-table-lspace: 0pt !important;\n" +
                "            mso-table-rspace: 0pt !important;\n" +
                "        }\n" +
                "        table:not(.button-table) {\n" +
                "            border-spacing: 0 !important;\n" +
                "            border-collapse: collapse !important;\n" +
                "            table-layout: fixed !important;\n" +
                "            margin: 0 auto !important;\n" +
                "        }\n" +
                "\n" +
                "        th {\n" +
                "            font-weight: normal;\n" +
                "        }\n" +
                "\n" +
                "        tr td p {\n" +
                "            margin: 0;\n" +
                "        }\n" +
                "\n" +
                "        img {\n" +
                "            -ms-interpolation-mode: bicubic;\n" +
                "        }\n" +
                "\n" +
                "        a[x-apple-data-detectors],\n" +
                "\n" +
                "        .unstyle-auto-detected-links a,\n" +
                "        .aBn {\n" +
                "            border-bottom: 0 !important;\n" +
                "            cursor: default !important;\n" +
                "            color: inherit !important;\n" +
                "            text-decoration: none !important;\n" +
                "            font-size: inherit !important;\n" +
                "            font-family: inherit !important;\n" +
                "            font-weight: inherit !important;\n" +
                "            line-height: inherit !important;\n" +
                "        }\n" +
                "\n" +
                "        .im {\n" +
                "            color: inherit !important;\n" +
                "        }\n" +
                "\n" +
                "        .a6S {\n" +
                "            display: none !important;\n" +
                "            opacity: 0.01 !important;\n" +
                "        }\n" +
                "\n" +
                "        img.g-img+div {\n" +
                "            display: none !important;\n" +
                "        }\n" +
                "\n" +
                "        @media only screen and (min-device-width: 320px) and (max-device-width: 374px) {\n" +
                "            u~div .contentMainTable {\n" +
                "                min-width: 320px !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media only screen and (min-device-width: 375px) and (max-device-width: 413px) {\n" +
                "            u~div .contentMainTable {\n" +
                "                min-width: 375px !important;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @media only screen and (min-device-width: 414px) {\n" +
                "            u~div .contentMainTable {\n" +
                "                min-width: 414px !important;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "\n" +
                "    <style>\n" +
                "        @media only screen and (max-device-width: 640px) {\n" +
                "            .contentMainTable {\n" +
                "                width: 100% !important;\n" +
                "                margin: auto !important;\n" +
                "            }\n" +
                "            .single-column {\n" +
                "                width: 100% !important;\n" +
                "                margin: auto !important;\n" +
                "            }\n" +
                "            .multi-column {\n" +
                "                width: 100% !important;\n" +
                "                margin: auto !important;\n" +
                "            }\n" +
                "            .imageBlockWrapper {\n" +
                "                width: 100% !important;\n" +
                "                margin: auto !important;\n" +
                "            }\n" +
                "        }\n" +
                "        @media only screen and (max-width: 640px) {\n" +
                "            .contentMainTable {\n" +
                "                width: 100% !important;\n" +
                "                margin: auto !important;\n" +
                "            }\n" +
                "            .single-column {\n" +
                "                width: 100% !important;\n" +
                "                margin: auto !important;\n" +
                "            }\n" +
                "            .multi-column {\n" +
                "                width: 100% !important;\n" +
                "                margin: auto !important;\n" +
                "            }\n" +
                "            .imageBlockWrapper {\n" +
                "                width: 100% !important;\n" +
                "                margin: auto !important;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "    <!--[if mso | IE]>\n" +
                "<style>\n" +
                ".button-NbM2kEcc64bf7pieUdilc { padding: 10px 16px; };\n" +
                ".button-NbM2kEcc64bf7pieUdilc a { margin: -10px -16px; }; </style>\n" +
                "<![endif]-->\n" +
                "    \n" +
                "<!--[if mso | IE]>\n" +
                "    <style>\n" +
                "        .list-block-outlook-outside-left {\n" +
                "            margin-left: -18px;\n" +
                "        }\n" +
                "    \n" +
                "        .list-block-outlook-outside-right {\n" +
                "            margin-right: -18px;\n" +
                "        }\n" +
                "\n" +
                "        a:link, span.MsoHyperlink {\n" +
                "            mso-style-priority:99;\n" +
                "            \n" +
                "  display: inline-block;  \n" +
                "    color: #5457FF;\n" +
                "    text-decoration: none;\n" +
                "    font-style: normal;\n" +
                "    font-weight: normal;\n" +
                "\n" +
                "        }\n" +
                "    </style>\n" +
                "<![endif]-->\n" +
                "\n" +
                "\n" +
                "    </head>\n" +
                "\n" +
                "    <body width=\"100%\" style=\"margin: 0; padding: 0 !important; mso-line-height-rule: exactly; background-color: #F5F6F8;\">\n" +
                "        <center role=\"article\" aria-roledescription=\"email\" lang=\"en\" style=\"width: 100%; background-color: #F5F6F8;\">\n" +
                "            <!--[if mso | IE]>\n" +
                "            <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" id=\"body_table\" style=\"background-color: #F5F6F8;\">\n" +
                "            <tbody>    \n" +
                "                <tr>\n" +
                "                    <td>\n" +
                "                    <![endif]-->\n" +
                "                        <table align=\"center\" role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"640\" style=\"margin: auto;\" class=\"contentMainTable\">\n" +
                "                            <tr class=\"wp-block-editor-spacerblock-v1\"><td style=\"background-color:#F5F6F8;line-height:50px;font-size:50px;width:100%;min-width:100%\">&nbsp;</td></tr><tr class=\"wp-block-editor-imageblock-v1\"><td style=\"background-color:#ffffff;padding-top:0;padding-bottom:0;padding-left:0;padding-right:0\" align=\"center\"><table align=\"center\" width=\"640\" class=\"imageBlockWrapper\" style=\"width:640px\" role=\"presentation\"><tbody><tr><td style=\"padding:0\"><img src=\"https://api.smtprelay.co/userfile/ea69f9c8-69b7-4b87-a1e9-8f67adb98273/finserve_mail_header.png\" width=\"640\" height=\"\" alt=\"\" style=\"border-radius:0px;display:block;height:auto;width:100%;max-width:100%;border:0\" class=\"g-img\"></td></tr></tbody></table></td></tr><tr class=\"wp-block-editor-paragraphblock-v1\"><td valign=\"top\" style=\"padding:14px 32px 32px 32px;background-color:#ffffff\"><p class=\"paragraph\" style=\"font-family:Helvetica, sans-serif;text-align:left;line-height:21.00px;font-size:14px;margin:0;color:#5f5f5f;letter-spacing:0;word-break:normal\"><span style=\"font-weight: bold\" class=\"bold\">Dear "+ employerContactName + ",</span><br><br>Trust this mail meets you well. Please be informed that one of your staff has applied for a loan with details below. Kindly verify the staff loan request using the verification button below;<br><br><br><span style=\"font-weight: bold\" class=\"bold\"><span style=\"display: inline-block;text-decoration: underline\" class=\"underline\">Staff Information</span></span><br>Staff Full name:                  <span style=\"font-weight: bold\" class=\"bold\"> </span> <span style=\"font-weight: bold\" class=\"bold\">{staffFullname}</span><br>Staff Phone Number:         <span style=\"font-weight: bold\" class=\"bold\">  "+staffPhoneNumber+"</span><br>Staff Employer:                    <span style=\"font-weight: bold\" class=\"bold\">{staffEmployer}</span><br><br><span style=\"font-weight: bold\" class=\"bold\"><span style=\"display: inline-block;text-decoration: underline\" class=\"underline\">Staff Loan Details</span></span><span style=\"display: inline-block;text-decoration: underline\" class=\"underline\"><span style=\"font-weight: bold\" class=\"bold\"></span></span><br>Loan Requested Amount:    <span style=\"font-weight: bold\" class=\"bold\">"+loanAmount+"</span><br>Loan Tenor:                         <span style=\"font-weight: bold\" class=\"bold\">"+loanTenor+"</span><br>Loan interest:                      <span style=\"font-weight: bold\" class=\"bold\">"+loanInterest+"</span><br>Total Repayment:                <span style=\"font-weight: bold\" class=\"bold\">"+loanRepayment+"</span></p></td></tr><tr class=\"wp-block-editor-buttonblock-v1\" align=\"center\"><td style=\"background-color:#ffffff;padding-top:16px;padding-right:20px;padding-bottom:10px;padding-left:20px;width:100%\" valign=\"top\"><table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" class=\"button-table\"><tbody><tr><td valign=\"top\" class=\"button-NbM2kEcc64bf7pieUdilc button-td button-td-primary\" style=\"cursor:pointer;border:none;border-radius:4px;background-color:#5457ff;font-size:16px;font-family:Open Sans, sans-serif;width:fit-content;text-decoration:none;color:#ffffff;overflow:hidden\"><a style=\"color:#ffffff;display:block;padding:10px 16px 10px 16px\" href=\"{verificationLink}\">Verify Staff Loan</a></td></tr></tbody></table></td></tr><tr class=\"wp-block-editor-paragraphblock-v1\"><td valign=\"top\" style=\"padding:20px 20px 20px 20px;background-color:#ffffff\"><p class=\"paragraph\" style=\"font-family:Helvetica, sans-serif;line-height:NaNpx;font-size:14px;margin:0;color:#5f5f5f;letter-spacing:0;word-break:normal\"><br>The Loan Team<br><br><span style=\"font-weight: bold\" class=\"bold\">Finserve Investment Loan Team</span></p></td></tr><tr><td valign=\"top\" align=\"center\" style=\"padding:20px 20px 20px 20px;background-color:#F5F6F8\"><p aria-label=\"Unsubscribe\" class=\"paragraph\" style=\"font-family:Open Sans, sans-serif;text-align:center;line-height:22.00px;font-size:11px;margin:0;color:#5f5f5f;word-break:normal\">If you no longer wish to receive mail from us, you can <a href=\"{unsubscribe}\" data-type=\"mergefield\" data-filename=\"\" data-id=\"c0c4d759-1c22-48d4-a614-785d6acaf420-6V201gHRzhDxAzaNqZiJS\" class=\"c0c4d759-1c22-48d4-a614-785d6acaf420-6V201gHRzhDxAzaNqZiJS\" data-mergefield-value=\"unsubscribe\" data-mergefield-input-value=\"\" style=\"color: #5457FF; display: inline-block;\">unsubscribe</a>.<br>{accountaddress}</p></td></tr><tr class=\"wp-block-editor-paragraphblock-v1\"><td valign=\"top\" style=\"padding:12px 12px 12px 12px;background-color:#F5F6F8\"><p class=\"paragraph\" style=\"font-family:Open Sans, sans-serif;text-align:center;line-height:11.50px;font-size:10px;margin:0;color:#5f5f5f;word-break:normal\">Unable to view? Read it <a href=\"{view}\" data-type=\"mergefield\" data-id=\"62d10d6d-b252-49a7-af10-c771dbd58b15-Xzt0XJLlayJkgPI0XyMI5\" data-filename=\"\" class=\"62d10d6d-b252-49a7-af10-c771dbd58b15-Xzt0XJLlayJkgPI0XyMI5\" data-mergefield-value=\"view\" data-mergefield-input-value=\"\" style=\"color: #5457FF; display: inline-block;\">Online</a></p></td></tr>\n" +
                "                        </table>\n" +
                "                    <!--[if mso | IE]>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "            </tbody>\n" +
                "            </table>\n" +
                "            <![endif]-->\n" +
                "        </center>\n" +
                "    </body>\n" +
                "</html>";
    }

    public void SendVerificationCode(String EmailAddress, String Fullname, String OtpValue) {

        String postUrl = "https://api.zeptomail.com/v1.1/email";
        String fromAddress = "noreply@finserveinvestment.com";
        String fromName = notification_sender_FromName;
        String subject = "Verify your registration account";

        BufferedReader br = null;
        HttpURLConnection conn = null;
        String output = null;
        StringBuffer sb = new StringBuffer();

        try {
            URL url = new URL(postUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", Zoho_API_Key);

            JSONObject object = new JSONObject("{\n" + "\"from\": { \"address\": " + fromAddress +", \"name\" : " + fromName + "},\n" +
                    "\"to\": [{\"email_address\": {\"address\": " + EmailAddress + ",\"name\": " + Fullname + "}}],\n" +
                    "\"subject\":"+subject+",\n" + "\"htmlbody\":\"<div><b> Dear Customer,</div><br><div>Kindly use the OTP below to complete your account registration verification. " +
                    "<br><br></div><h4><u>Registration OTP</u></h4>" +
                    "<b>" + OtpValue + "</b><br><br>" +
                    "<br><b>Account Team</b><br><b>Finserve Investment Limited</b>\"\n" + "}");

            OutputStream os = conn.getOutputStream();
            os.write(object.toString().getBytes());
            os.flush();
            br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            System.out.println(sb.toString());
        } catch (Exception e) {
            br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
            //while ((output = br.readLine()) != null) {
            //  sb.append(output);
            //}
            System.out.println(sb.toString());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    public void SendPasswordResetNotification(String Fullname, String EmailAddress, String NewPinNumber) {

        String postUrl = "https://api.zeptomail.com/v1.1/email";
        String fromAddress = "noreply@finserveinvestment.com";
        String fromName = notification_sender_FromName;
        String subject = "PIN Reset Notification";

        BufferedReader br = null;
        HttpURLConnection conn = null;
        String output = null;
        StringBuffer sb = new StringBuffer();

        try {
            URL url = new URL(postUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", Zoho_API_Key);

            JSONObject object = new JSONObject("{\n" + "\"from\": { \"address\": " + fromAddress +", \"name\" : " + fromName + "},\n" +
                    "\"to\": [{\"email_address\": {\"address\": " + EmailAddress + ",\"name\": " + Fullname + "}}],\n" +
                    "\"subject\":"+subject+",\n" + "\"htmlbody\":\"<div><b> Dear " + Fullname + ",</div><br><div>We received a request to reset the pin number to your account. Kindly ingore this email if you do not make the request. " +
                    "<br><br>Kindly enter the new pin below to continue with your request.</div><h4><u>New Pin Details</u></h4>" +
                    "New Pin: <b>" + NewPinNumber + "</b><br><br>" +
                    "<br><b>Account Team</b><br><b>Finserve Investment Limited</b>\"\n" + "}");

            OutputStream os = conn.getOutputStream();
            os.write(object.toString().getBytes());
            os.flush();
            br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            System.out.println(sb.toString());
        } catch (Exception e) {
            br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
            //while ((output = br.readLine()) != null) {
            //  sb.append(output);
            //}
            System.out.println(sb.toString());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    public void sendnewemail(String EmailAddress, String staffPhoneNumber, String staffEmployer,
                             String staffFullName, String Sector, String GradeLevel, String ServiceLength,
                             String StaffID, String AnnualSalary, String SalaryPaymentDate,
                             String verificationLink,
                             String loanAmount, String loanTenor, String loanInterest, String MonthlyRepayment,
                             String loanRepayment, String employerContactName) {

        String postUrl = "https://api.zeptomail.com/v1.1/email";
        String fromAddress = "loanapplication@finserveinvestment.com";
        String fromName = notification_sender_FromName;
        String subject = "Staff Loan Request Verification";

        BufferedReader br = null;
        HttpURLConnection conn = null;
        String output = null;
        StringBuffer sb = new StringBuffer();

        try {
            URL url = new URL(postUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", Zoho_API_Key);

            JSONObject object = new JSONObject("{\n" + "\"from\": { \"address\": " + fromAddress +", \"name\" : " + fromName + "},\n" +
                    "\"to\": [{\"email_address\": {\"address\": " + EmailAddress + ",\"name\": " + employerContactName + "}}],\n" +
                    "\"subject\":"+subject+",\n" + "\"htmlbody\":\"<div><b> Dear " + employerContactName + ",</div><br><div>Trust this mail meets you well. " +
                    "Please be informed that one of your staff has applied for a loan with details below. Kindly verify the staff " +
                    "loan request using the verification button below</div><br><h4><u>Staff Information</u></h4>" +
                    "Staff Fullname: <b>" + staffFullName + "</b><br>" +
                    "Staff Phone Number: <b>" + staffPhoneNumber + "</b><br>" +
                    "Staff ID Number: <b>" + StaffID + "</b><br>" +
                    "Sector: <b>" + Sector + "</b><br>" +
                    "Grade Level: <b>" + GradeLevel + "</b><br>" +
                    "Service Length: <b>" + ServiceLength + "</b><br>" +
                    "Annual Salary: <b>" + AnnualSalary + "</b><br>" +
                    "Salary Payment Date: <b>" + SalaryPaymentDate + "</b><br>" +
                    "Staff Employer: <b>"+ staffEmployer + "</b><br><br><h4><u>Staff Loan Application Details</u></h4>" +
                    "Loan Amount: <b>"+loanAmount+"</b><br>" +
                    "Loan Tenor: <b>"+loanTenor+" Month(s)</b><br>" +
                    "Loan Interest: <b>"+loanInterest+"</b><br>" +
                    "Monthly Repayment: <b>"+MonthlyRepayment+"</b><br>" +
                    "Total Repayment: <b>"+loanRepayment+"</b><br><br><br><a title='Verify Staff Loan' href="+verificationLink+">Click here to verify staff loan</a>" +
                    "<br><br><br><b>Loan Team</b><br><b>Finserve Investment Limited</b>\"\n" + "}");

            OutputStream os = conn.getOutputStream();
            os.write(object.toString().getBytes());
            os.flush();
            br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            System.out.println(sb.toString());
        } catch (Exception e) {
            br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
            //while ((output = br.readLine()) != null) {
              //  sb.append(output);
            //}
            System.out.println(sb.toString());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }// service to send email

    // service to send email via zoho mail
    public void SendStaffLoanVerificationNotificationZoho() {

        String fromAddress = "loanapplication@finserveinvestment.com";
        String fromName = notification_sender_FromName;
        String subject = "Staff Loan Request Verification";
        String APIKey = Zoho_API_Key;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", APIKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        //ZohoMailBody body = new ZohoMailBody();

        /*
        JSONObject body = new JSONObject();
        body.put("from", fromAddress);
        body.put("to", "[{\"email_address\": {\"address\": \"tunslike@gmail.com\",\"name\": \"Babatunde Francis\"}}]");
        body.put("subject", subject);
        body.put("htmlbody", "<div><b> Test email from code sent successfully. </b></div>"); */

        /*
        body.setTo("[{\"email_address\": {\"address\": \"tunslike@gmail.com\",\"name\": \"Babatunde Francis\"}}]");
        body.setFrom(fromAddress);
        body.setSubject(subject);
        body.setHtmlbody("<div><b> Test email from code sent successfully. </b></div>"); */

        Map<String, Object> reqbody =  new HashMap<>();

        reqbody.put("from", fromAddress);
        reqbody.put("to", "[{\"email_address\": {\"address\": \"tunslike@gmail.com\",\"name\": \"Babatunde Francis\"}}]");
        reqbody.put("subject", subject);
        reqbody.put("htmlbody", "<div><b> Test email from code sent successfully. </b></div>");

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.zeptomail.com/v1.1/email";

        try {

            Gson gson = new Gson();
            HttpEntity<String> request = new HttpEntity<String>(gson.toJson(reqbody),headers);

                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST,
                    request, String.class);

            String responsebody = response.getBody();

        }catch (Exception e) {
        logger.error(e.getMessage());
    }
    }
    // end of service

    // service to send account registration
    public void SendAccountRegistrationNotification(String EmailAddress, String password, String firstName) {

        String fromName = notification_sender_FromName;
        String fromAddress = notification_sender_FromAddress;
        String subject = "New Account Registration";
        String APIKey = notification_api_key;
        String templateName = "Finserve_New_Registration";

        try{

            final String uri_template = notification_sender_sendBaseUrl + APIKey + "&subject=" + subject + "&msgFrom=" + fromAddress + "&msgFromName="
                    + fromName + "&template=" + templateName + "&msgTo=" + EmailAddress + "&merge_clientName=" + firstName +
                    "&merge_accountUsername=" + EmailAddress + "&merge_accountPinNumber=" + password;

            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri_template, String.class);

        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
    // end of service
}
