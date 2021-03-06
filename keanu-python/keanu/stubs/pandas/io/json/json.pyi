# Stubs for pandas.io.json.json (Python 3.6)
#
# NOTE: This dynamically typed stub was automatically generated by stubgen.

from .normalize import _convert_to_line_delimits
from .table_schema import build_table_schema, parse_table_schema
from pandas.io.common import BaseIterator
from typing import Any, Optional

loads: Any
dumps: Any
TABLE_SCHEMA_VERSION: str

def to_json(path_or_buf: Any, obj: Any, orient: Optional[Any] = ..., date_format: str = ..., double_precision: int = ..., force_ascii: bool = ..., date_unit: str = ..., default_handler: Optional[Any] = ..., lines: bool = ..., compression: str = ..., index: bool = ...): ...

class Writer:
    obj: Any = ...
    orient: Any = ...
    date_format: Any = ...
    double_precision: Any = ...
    ensure_ascii: Any = ...
    date_unit: Any = ...
    default_handler: Any = ...
    index: Any = ...
    is_copy: Any = ...
    def __init__(self, obj: Any, orient: Any, date_format: Any, double_precision: Any, ensure_ascii: Any, date_unit: Any, index: Any, default_handler: Optional[Any] = ...) -> None: ...
    def write(self): ...

class SeriesWriter(Writer): ...
class FrameWriter(Writer): ...

class JSONTableWriter(FrameWriter):
    schema: Any = ...
    obj: Any = ...
    date_format: str = ...
    orient: str = ...
    index: Any = ...
    def __init__(self, obj: Any, orient: Any, date_format: Any, double_precision: Any, ensure_ascii: Any, date_unit: Any, index: Any, default_handler: Optional[Any] = ...) -> None: ...

def read_json(path_or_buf: Optional[Any] = ..., orient: Optional[Any] = ..., typ: str = ..., dtype: bool = ..., convert_axes: bool = ..., convert_dates: bool = ..., keep_default_dates: bool = ..., numpy: bool = ..., precise_float: bool = ..., date_unit: Optional[Any] = ..., encoding: Optional[Any] = ..., lines: bool = ..., chunksize: Optional[Any] = ..., compression: str = ...): ...

class JsonReader(BaseIterator):
    path_or_buf: Any = ...
    orient: Any = ...
    typ: Any = ...
    dtype: Any = ...
    convert_axes: Any = ...
    convert_dates: Any = ...
    keep_default_dates: Any = ...
    numpy: Any = ...
    precise_float: Any = ...
    date_unit: Any = ...
    encoding: Any = ...
    compression: Any = ...
    lines: Any = ...
    chunksize: Any = ...
    nrows_seen: int = ...
    should_close: bool = ...
    data: Any = ...
    def __init__(self, filepath_or_buffer: Any, orient: Any, typ: Any, dtype: Any, convert_axes: Any, convert_dates: Any, keep_default_dates: Any, numpy: Any, precise_float: Any, date_unit: Any, encoding: Any, lines: Any, chunksize: Any, compression: Any) -> None: ...
    def read(self): ...
    def close(self) -> None: ...
    def __next__(self): ...

class Parser:
    json: Any = ...
    orient: Any = ...
    dtype: Any = ...
    min_stamp: Any = ...
    numpy: Any = ...
    precise_float: Any = ...
    convert_axes: Any = ...
    convert_dates: Any = ...
    date_unit: Any = ...
    keep_default_dates: Any = ...
    obj: Any = ...
    def __init__(self, json: Any, orient: Any, dtype: bool = ..., convert_axes: bool = ..., convert_dates: bool = ..., keep_default_dates: bool = ..., numpy: bool = ..., precise_float: bool = ..., date_unit: Optional[Any] = ...) -> None: ...
    def check_keys_split(self, decoded: Any) -> None: ...
    def parse(self): ...

class SeriesParser(Parser): ...
class FrameParser(Parser): ...
