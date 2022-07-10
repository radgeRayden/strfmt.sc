using import enum
using import String
using import Array
using import struct

enum TemplateChunk
    Text : String
    Code : String

let C = (import C.string)
let stbsp = (import stb.sprintf)

let start-token end-token = "${" "}"

""""Get the inner code inside a template.
    @param start rawstring @pointer to start of template, at start-token
    @param end rawstring @pointer to end of template, at end-token
    @return (:_ rawstring usize)
fn get-inner (start end)
    let start-offset end-offset = (ptrtoint start usize) (ptrtoint end usize)
    _
        inttoptr (start-offset + (countof start-token)) rawstring
        end-offset - start-offset - (countof start-token)

inline ptr-offset (start end)
    (ptrtoint end usize) - (ptrtoint start usize)

""""Parse a templated string into a list of interleaved code strings.
    @param input (array i8) @constant string literal containing the templated string
    @return (Array TemplateChunk)
fn parse-template (input)
    local result : (Array TemplateChunk)

    # (prefix .. code .. suffix)+ | input
    loop (next = (input as rawstring))
        let start = (C.strstr next start-token)
        if (start != null)
            let end = (C.strstr start end-token)
            if (end != null)
                # add anything between current and previous
                if (next != start)
                    let prefix = (String next (ptr-offset next start))
                    'append result (TemplateChunk.Text prefix)

                let inner len = (get-inner start end)
                'append result (TemplateChunk.Code (String inner len))
                repeat
                    as
                        & (end @ (countof end-token))
                        rawstring

        # no more templates
        let next-idx = (ptr-offset (input as rawstring) next)
        if (next-idx == (countof input))
            break;
        else
            let remainder = (String next ((countof input) - next-idx))
            'append result (TemplateChunk.Text remainder)
            break;

    result

inline prefix:f (str)
    print (parse-template str)
    String str

do
    let prefix:f
    locals;
