package pl.wavesoftware.example.code;

import org.apiguardian.api.API;
import org.springframework.stereotype.Service;

import static org.apiguardian.api.API.Status.STABLE;

@Service
@API(status = STABLE)
public class HelloService {
  String hello() {
    return "Hello from Plug";
  }
}
