package ba.unze.kosnica.controller;
import ba.unze.kosnica.model.StoredFile;
import ba.unze.kosnica.model.User;
import ba.unze.kosnica.service.FileService;
import ba.unze.kosnica.service.UserService;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;
    private final UserService userService;

    public FileController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @GetMapping
    public String list(Principal principal, Model model) {
        User user = userService.getByEmail(principal.getName());
        model.addAttribute("files", fileService.listFor(user));
        return "files";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         Principal principal, RedirectAttributes redirect) {
        User user = userService.getByEmail(principal.getName());
        try {
            fileService.store(file, user);
            redirect.addFlashAttribute("message", "Med je donesen u košnicu!");
        } catch (IllegalArgumentException | IOException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/files";
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        StoredFile storedFile = fileService.getOwned(id, user);
        Resource resource = fileService.loadAsResource(storedFile);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(storedFile.getOriginalName()).build().toString())
                .body(resource);
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal,
                         RedirectAttributes redirect) {
        User user = userService.getByEmail(principal.getName());
        try {
            fileService.delete(id, user);
            redirect.addFlashAttribute("message", "Ćelija je obrisana.");
        } catch (IllegalArgumentException | IOException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/files";
    }


}
