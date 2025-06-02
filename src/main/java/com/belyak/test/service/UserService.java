package com.belyak.test.service;

import com.belyak.test.dto.EditUserDto;
import com.belyak.test.dto.ReadUserDto;
import com.belyak.test.exception.EntityNotFoundException;
import com.belyak.test.mapper.UserMapper;
import com.belyak.test.model.Country;
import com.belyak.test.model.User;
import com.belyak.test.repository.CountryRepository;
import com.belyak.test.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.belyak.test.model.enums.StatusCodes.ENTITY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;


    public ReadUserDto getByUsername(String username) {
        return this.userRepository.findByUsername(username)
                .map(userMapper::toReadUserDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "User with username %s not found".formatted(username)));
    }

    public void updateUser(String username, EditUserDto userDto) {
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "User with username %s not found".formatted(username)));

        user.setFirstname(userDto.getFirstname());
        user.setLastname(userDto.getLastname());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setBio(userDto.getBio());

        Country country = countryRepository.findByCode(userDto.getCountryCode())
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "Country with code %s not found".formatted(userDto.getCountryCode())));
        user.setCountry(country);

        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository
                .findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_NOT_FOUND.name(),
                        "User with username %s not found".formatted(username)));
    }

}
