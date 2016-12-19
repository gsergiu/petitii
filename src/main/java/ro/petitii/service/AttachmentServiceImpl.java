package ro.petitii.service;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.petitii.config.EmailAttachmentConfig;
import ro.petitii.model.Attachment;
import ro.petitii.repository.AttachmentRepository;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.*;
import java.util.Date;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    @Autowired
    AttachmentRepository attachmentRepository;

    @Autowired
    EmailAttachmentConfig config;

    @PersistenceContext
    EntityManager em;

    private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentServiceImpl.class);

    @Override
    @Transactional
    public Attachment saveFromEmail(Attachment a) {
        LOGGER.info("Email id: " + a.getEmail().getId());
        prepFolder();
        BodyPart attBody = a.getBodyPart();
        try {
            LOGGER.info("Getting attachment");
            a.setOriginalFilename(attBody.getFileName());
        } catch (MessagingException e2) {
            LOGGER.info("Could not parse message: " + e2.getMessage());
        }
        em.persist(a);
        em.flush();
        String extension = FilenameUtils.getExtension(a.getOriginalFilename());
        try {
            String filename = FilenameUtils.concat(config.getPath(),a.getId() + "." + extension);
            //((MimeBodyPart) attBody).saveFile(filename);
            BufferedInputStream in = new BufferedInputStream(attBody.getInputStream());
            OutputStream os = new FileOutputStream(filename);
            BufferedOutputStream out = new BufferedOutputStream(os);
            byte[] chunk = new byte[500000];
            int available;
            while ((available = in.read(chunk))!=-1) {
                out.write(chunk,0,available);
            }
            out.close();
            os.close();
            LOGGER.info("Saved file to disk");
            a.setFilename(filename);
            a.setContentType(attBody.getContentType());
        } catch (IOException e1) {
            LOGGER.info("Could not saveFromEmail file: " + e1.getMessage());
        } catch (MessagingException e2) {
            LOGGER.info("Could not parse message: " + e2.getMessage());
        }
        return attachmentRepository.save(a);
    }

    @Override
    public Attachment findById(Long id) {
        return attachmentRepository.findOne(id);
    }

    private void prepFolder() {
        LOGGER.info("Preparing to saveFromEmail attachment in folder: " + config.getPath());
        File target = new File(config.getPath());
        if (!target.isDirectory()) {
            LOGGER.info("Creating directory structure: " + config.getPath());
            if (!target.mkdirs()) {
                LOGGER.error("Failed to create directory structure: " + config.getPath());
            }
        }
    }
}