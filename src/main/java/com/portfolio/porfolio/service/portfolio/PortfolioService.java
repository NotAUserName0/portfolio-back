package com.portfolio.porfolio.service.portfolio;

import java.util.List;

import org.springframework.stereotype.Service;
import com.portfolio.porfolio.repository.PortfolioRepository;
import org.springframework.web.multipart.MultipartFile;

import com.portfolio.porfolio.dto.LinkFormDto;
import com.portfolio.porfolio.dto.PortfolioDto;
import com.portfolio.porfolio.dto.ProjectFormDto;
import com.portfolio.porfolio.models.Portfolio;
import com.portfolio.porfolio.utils.ApiResponse;
import com.portfolio.porfolio.utils.Constants;
import com.portfolio.porfolio.utils.HttpStatuses;
import com.portfolio.porfolio.utils.Components.DtoMapper;
import com.portfolio.porfolio.utils.Components.FileStorageService;

/**
 * @description Service handling portfolio business logic, data mapping, and storage delegation.
 */
@Service
public class PortfolioService {

    // Dependencies injected via constructor
    private final PortfolioRepository portfolioRepository;
    private final DtoMapper dtoMapper;
    private final FileStorageService fileStorageService;

    /**
     * @description Injects required dependencies for portfolio management and file persistence.
     * @param portfolioRepository Repository for accessing Portfolio database entities.
     * @param dtoMapper Component for entity-to-DTO and DTO-to-entity mapping.
     * @param fileStorageService Reusable component for secure file uploads and validation.
     */
    public PortfolioService(
            PortfolioRepository portfolioRepository,
            DtoMapper dtoMapper,
            FileStorageService fileStorageService) {
        this.portfolioRepository = portfolioRepository;
        this.dtoMapper = dtoMapper;
        this.fileStorageService = fileStorageService;
    }

    /**
     * @description Gives portafolio data with saved files for profile, projects,
     *              and links.
     * @param data        The portfolio data object to be populated with saved file
     *                    paths.
     * @param profileFile The profile image file for the portfolio.
     * @param proyectFile The list of project image files.
     * @param linkFile    The list of link icon files for the portfolio's general
     *                    links.
     * @param iconFile    The list of icon files for the internal project links.
     * @return An ApiResponse containing the success message and the populated
     *         portfolio data.
     */
    public ApiResponse<Object> createPortfolioService(
            PortfolioDto data,
            MultipartFile profileFile,
            List<MultipartFile> proyectFile,
            List<MultipartFile> linkFile,
            List<MultipartFile> iconFile) {

        Portfolio existingPortfolio = null;
        if (data.getId() != 0) {
            existingPortfolio = portfolioRepository.findById(data.getId()).orElse(null);
        }
        if (existingPortfolio == null) {
            existingPortfolio = portfolioRepository.findFirstByOrderByIdAsc().orElse(null);
        }

        if (existingPortfolio != null) {
            return updatePortfolioService(existingPortfolio.getId(), data, profileFile, proyectFile, linkFile, iconFile);
        }

        if (profileFile != null && !profileFile.isEmpty()) {
            data.setFile(fileStorageService.saveFile(profileFile));
        }

        if (data.getProjects() != null) {
            List<ProjectFormDto> proyectos = data.getProjects();
            int projectFileIndex = Constants.ZERO;

            for (int i = Constants.ZERO; i < proyectos.size(); i++) {
                ProjectFormDto proyecto = proyectos.get(i);

                MultipartFile matchedFile = findUploadedFile(proyectFile, proyecto.getFile());
                if (matchedFile != null) {
                    proyecto.setFile(fileStorageService.saveFile(matchedFile));
                } else if (proyectFile != null && projectFileIndex < proyectFile.size() && !proyectFile.get(projectFileIndex).isEmpty()) {
                    proyecto.setFile(fileStorageService.saveFile(proyectFile.get(projectFileIndex++)));
                }

                // Project links do NOT have custom icons
                if (proyecto.getLinks() != null) {
                    for (LinkFormDto link : proyecto.getLinks()) {
                        link.setIcon(null);
                    }
                }
            }
        }

        // Social links: receive iconFile (the files for social links)
        if (data.getLinks() != null) {
            List<LinkFormDto> linksGenerales = data.getLinks();
            int iconIndex = Constants.ZERO;
            for (int i = Constants.ZERO; i < linksGenerales.size(); i++) {
                LinkFormDto link = linksGenerales.get(i);
                MultipartFile matchedIcon = findUploadedFile(iconFile, link.getIcon());
                if (matchedIcon != null) {
                    link.setIcon(fileStorageService.saveFile(matchedIcon));
                } else if (iconFile != null && iconIndex < iconFile.size() && !iconFile.get(iconIndex).isEmpty()) {
                    link.setIcon(fileStorageService.saveFile(iconFile.get(iconIndex++)));
                }
            }
        }

        Portfolio portfolio = new Portfolio();
        portfolio = dtoMapper.map(data, portfolio.getClass());

        portfolioRepository.save(portfolio);

        return ApiResponse.success(PortfolioMessages.PORTFOLIO_SUCCESS, data);
    }

    /**
     * @description Retrieves the primary portfolio from the repository using an optimized single-record query.
     *              Mitigates DoS risk by avoiding loading the entire table into memory via findAll().
     * @return ApiResponse containing the first portfolio if found, otherwise an error message.
     */
    public ApiResponse<Object> getPortfolio() {
        Portfolio portfolio = portfolioRepository.findFirstByOrderByIdAsc().orElse(null);

        if (portfolio == null) {
            return ApiResponse.error(HttpStatuses.NOT_FOUND, PortfolioMessages.PORTFOLIO_NOT_FOUND);
        }

        // Normalize legacy image paths that might be missing the "upload/" prefix in the database
        normalizeImagePaths(portfolio);

        return ApiResponse.success(PortfolioMessages.PORTFOLIO_SUCCESS, portfolio);
    }

    /**
     * @description Normalizes file and icon paths in the portfolio entity so they consistently
     *              include the "upload/" prefix, fixing 403 Forbidden errors when loading legacy images.
     * @param portfolio The portfolio entity whose image paths will be verified and normalized.
     */
    private void normalizeImagePaths(Portfolio portfolio) {
        if (portfolio == null) {
            return;
        }

        portfolio.setFile(ensureUploadPrefix(portfolio.getFile()));

        if (portfolio.getProjects() != null) {
            portfolio.getProjects().forEach(project -> {
                project.setFile(ensureUploadPrefix(project.getFile()));
                if (project.getLinks() != null) {
                    project.getLinks().forEach(link -> link.setIcon(null));
                }
            });
        }

        if (portfolio.getLinks() != null) {
            portfolio.getLinks().forEach(link -> link.setIcon(ensureUploadPrefix(link.getIcon())));
        }
    }

    /**
     * @description Ensures a relative image filename is prefixed with "upload/".
     * @param path The raw path stored in the database.
     * @return Normalized path prefixed with "upload/".
     */
    private String ensureUploadPrefix(String path) {
        if (path == null || path.isBlank() || path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }
        return path.startsWith("upload/") ? path : "upload/" + path;
    }

    /**
     * @description Matches an uploaded file from a list by its original filename.
     * @param files The list of uploaded multipart files.
     * @param targetFilename The expected filename.
     * @return The matched MultipartFile, or null if not found.
     */
    private MultipartFile findUploadedFile(List<MultipartFile> files, String targetFilename) {
        if (files == null || targetFilename == null || targetFilename.isBlank()) {
            return null;
        }
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty() && targetFilename.equals(file.getOriginalFilename())) {
                return file;
            }
        }
        return null;
    }

    public ApiResponse<Object> updatePortfolioService(
            Integer id,
            PortfolioDto data,
            MultipartFile profileFile,
            List<MultipartFile> proyectFile,
            List<MultipartFile> linkFile,
            List<MultipartFile> iconFile) {

        Portfolio portfolioExistente = portfolioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(HttpStatuses.NOT_FOUND + ": " + PortfolioMessages.PORTFOLIO_NOT_FOUND));

        if (profileFile != null && !profileFile.isEmpty()) {
            String nuevoFile = fileStorageService.saveFile(profileFile);
            data.setFile(nuevoFile);
        } else {
            data.setFile(portfolioExistente.getFile());
        }

        if (data.getProjects() != null) {
            List<ProjectFormDto> proyectosDto = data.getProjects();
            int projectFileIndex = Constants.ZERO;

            for (int i = Constants.ZERO; i < proyectosDto.size(); i++) {
                ProjectFormDto proyectoDto = proyectosDto.get(i);

                MultipartFile matchedFile = findUploadedFile(proyectFile, proyectoDto.getFile());
                if (matchedFile != null) {
                    proyectoDto.setFile(fileStorageService.saveFile(matchedFile));
                } else if (proyectFile != null && projectFileIndex < proyectFile.size() && !proyectFile.get(projectFileIndex).isEmpty()) {
                    proyectoDto.setFile(fileStorageService.saveFile(proyectFile.get(projectFileIndex++)));
                } else {
                    if (proyectoDto.getFile() == null && portfolioExistente.getProjects() != null && i < portfolioExistente.getProjects().size()) {
                        proyectoDto.setFile(portfolioExistente.getProjects().get(i).getFile());
                    }
                }

                if ((proyectoDto.getName() == null || proyectoDto.getName().trim().isEmpty())
                        && portfolioExistente.getProjects() != null
                        && i < portfolioExistente.getProjects().size()) {
                    proyectoDto.setName(portfolioExistente.getProjects().get(i).getName());
                }

                // Project links do NOT have custom icons
                if (proyectoDto.getLinks() != null) {
                    for (LinkFormDto link : proyectoDto.getLinks()) {
                        link.setIcon(null);
                    }
                }
            }
        }

        // Social links: use iconFile (from @RequestPart("icon"))
        if (data.getLinks() != null) {
            List<LinkFormDto> linksGenerales = data.getLinks();
            int iconIndex = Constants.ZERO;
            for (int i = Constants.ZERO; i < linksGenerales.size(); i++) {
                LinkFormDto linkDto = linksGenerales.get(i);
                MultipartFile matchedIcon = findUploadedFile(iconFile, linkDto.getIcon());
                if (matchedIcon != null) {
                    linkDto.setIcon(fileStorageService.saveFile(matchedIcon));
                } else if (iconFile != null && iconIndex < iconFile.size() && !iconFile.get(iconIndex).isEmpty()) {
                    linkDto.setIcon(fileStorageService.saveFile(iconFile.get(iconIndex++)));
                } else {
                    if ((linkDto.getIcon() == null || linkDto.getIcon().isEmpty())
                            && portfolioExistente.getLinks() != null
                            && i < portfolioExistente.getLinks().size()) {
                        linkDto.setIcon(portfolioExistente.getLinks().get(i).getIcon());
                    }
                }
            }
        }

        Portfolio portfolioMapeado = dtoMapper.map(data, Portfolio.class);
        portfolioMapeado.setId(id);
        Portfolio portfolioActualizado = portfolioRepository.save(portfolioMapeado);

        return ApiResponse.success(PortfolioMessages.PORTFOLIO_UPDATE_SUCCESS, portfolioActualizado);
    }

    public ApiResponse<Object> deletePortfolioService(Integer id) {
        Portfolio portfolioExistente = portfolioRepository.findById(id).orElse(null);

        if (portfolioExistente == null) {
            return ApiResponse.error(PortfolioMessages.PORTFOLIO_NOT_FOUND);
        }

        portfolioRepository.deleteById(id);

        return ApiResponse.success(PortfolioMessages.PORTFOLIO_DELETE_SUCCESS, null);
    }
}
