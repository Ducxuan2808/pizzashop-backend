package com.example.pizzashop.controllers;

import com.example.pizzashop.components.LocalizationUtils;
import com.example.pizzashop.dtos.PizzaDTO;
import com.example.pizzashop.dtos.PizzaImageDTO;
import com.example.pizzashop.models.Pizza;
import com.example.pizzashop.models.PizzaImage;
import com.example.pizzashop.responses.PizzaListResponse;
import com.example.pizzashop.responses.PizzaResponse;
import com.example.pizzashop.services.IPizzaService;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("pizzas")
@RequiredArgsConstructor
public class PizzaController {
    private final IPizzaService pizzaService;
    private final LocalizationUtils localizationUtils;

    @PostMapping( "")
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody PizzaDTO pizzaDTO,
            //@ModelAttribute("files") List<MultipartFile> files,
            //@RequestPart("file") MultipartFile files,
            BindingResult result){
        try{
            if(!pizzaService.existByName(pizzaDTO.getName())){
                if(result.hasErrors()){
                    List<String> errorMessages = result.getFieldErrors()
                            .stream()
                            .map(FieldError::getDefaultMessage)
                            .toList();
                    return ResponseEntity.badRequest().body(errorMessages);
                }
                Pizza newProduct =  pizzaService.createPizza(pizzaDTO);
                return ResponseEntity.ok(newProduct);
            }
            else {
                return ResponseEntity.badRequest().body("Tên pizza đã tồn tại");
            }
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("")
    public ResponseEntity<PizzaListResponse> getPizzas(
            @RequestParam(defaultValue = "", name = "sort_by") String sortBy,
            @RequestParam(defaultValue = "", name = "min_price") Float minPrice,
            @RequestParam(defaultValue = "", name = "max_price") Float maxPrice,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int limit
    ){
        PageRequest pageRequest = PageRequest.of(page,limit, Sort.by("id").ascending());
        Page<PizzaResponse> pizzaPage = pizzaService.getAllPizzas(sortBy, minPrice, maxPrice, keyword, pageRequest);
        //lay tong so trang
        int totalPages = pizzaPage.getTotalPages();
        List<PizzaResponse> pizzas =  pizzaPage.getContent();
        return ResponseEntity.ok(PizzaListResponse
                .builder()
                .pizzas(pizzas)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long pizzaId){
        try {
            Pizza existingPizza =  pizzaService.getPizzaById(pizzaId);
            return ResponseEntity.ok(PizzaResponse.fromPizza(existingPizza));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
                                           @RequestBody PizzaDTO pizzaDTO){
        try {
            Pizza existingPizza =  pizzaService.updatePizza(id,pizzaDTO);
            return ResponseEntity.ok(PizzaResponse.fromPizza(existingPizza));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id){
        try {
            pizzaService.deletePizza(id);
            return ResponseEntity.ok("Pizza đã xóa thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/by-ids")
    public ResponseEntity<?> getPizzasByIds(@RequestParam("ids") String ids){
        try {
            //tach chuoi
            List<Long> pizzasIds = Arrays.stream(ids.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Pizza> pizzas = pizzaService.findPizzasByIds(pizzasIds);
            return ResponseEntity.ok(pizzas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String storeFile(MultipartFile file) throws IOException {
        if(!isImageFile(file)||file.getOriginalFilename()==null){
            throw new IOException("Invalid image format");
        }
        String filename= StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uniqueFilename= UUID.randomUUID().toString()+"_"+filename;
        Path uploadDir = Paths.get("uploads");
        if(!Files.exists(uploadDir)){
            Files.createDirectories(uploadDir);
        }
        Path destination = Paths.get(uploadDir.toString(),uniqueFilename);
        Files.copy(file.getInputStream(),destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    private boolean isImageFile(MultipartFile file){
        String contentType= file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    @PostMapping(value = "/uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadImages(
            @PathVariable("id") Long pizzaId,
            @RequestParam("files") List<MultipartFile> files) {
        try {
            Pizza existingPizza = pizzaService.getPizzaById(pizzaId);
            files = files == null ? new ArrayList<MultipartFile>() : files;
            if (files.size() > PizzaImage.MAXIMUM_IMAGES_PER_PRODUCT) {
                return ResponseEntity.badRequest().body(localizationUtils.getLocalizationMessage(
                        "Bạn chỉ có thể tải lên tối đa 5 hình ảnh"));
            }
            List<PizzaImage> productImages = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file.getSize() == 0) {
                    continue;
                }
                if (file.getSize() > 10 * 1024 * 1024) {

                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(localizationUtils
                            .getLocalizationMessage("Tập tin quá lớn! Kích thước tối đa là 10MB"));
                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(
                            localizationUtils.getLocalizationMessage("Tập tin phải là hình ảnh")
                    );
                }
                //luu file va cap nhat thumbnail
                String filename = storeFile(file);
                // productDTO.setThumbnail(filename);
                //luu vao doi tuong trong database
                PizzaImage productImage = pizzaService.createPizzaImage(
                        existingPizza.getId(),
                        PizzaImageDTO.builder()
                                .pizzaId(existingPizza.getId())
                                .imageUrl(filename)
                                .build());
                //luu vao bang product_image
                productImages.add(productImage);
            }
            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping({"/images", "/images/", "/images/{imageName}"})
    public ResponseEntity<?> viewImage(@PathVariable(required = false) String imageName) {
        try {
            // Check if imageName is null, empty, or the path is just "/images/"
            if (imageName == null || imageName.isEmpty()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpg").toUri()));
            }

            java.nio.file.Path imagePath = Paths.get("uploads/" + imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpg").toUri()));
            }
        } catch (Exception e) {
            try {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpg").toUri()));
            } catch (Exception ex) {
                return ResponseEntity.notFound().build();
            }
        }
    }

    @GetMapping("/best-seller")
    public ResponseEntity<PizzaListResponse> getBestSellPizzas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int limit
    ){
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<PizzaResponse> pizzaPage = pizzaService.bestSellPizzas(pageRequest);
        //lay tong so trang
        int totalPages = pizzaPage.getTotalPages();
        List<PizzaResponse> pizzas =  pizzaPage.getContent();
        return ResponseEntity.ok(PizzaListResponse
                .builder()
                .pizzas(pizzas)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("/worst-seller")
    public ResponseEntity<PizzaListResponse> worstSellPizzas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int limit
    ){
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<PizzaResponse> pizzaPage = pizzaService.worstSellPizzas(pageRequest);
        //lay tong so trang
        int totalPages = pizzaPage.getTotalPages();
        List<PizzaResponse> pizzas =  pizzaPage.getContent();
        return ResponseEntity.ok(PizzaListResponse
                .builder()
                .pizzas(pizzas)
                .totalPages(totalPages)
                .build());
    }
}
