/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.renderer.support;

import kunlun.data.bean.BeanUtils;
import kunlun.exception.ExceptionUtils;
import kunlun.io.util.IOUtils;
import kunlun.time.DateUtils;
import kunlun.util.Assert;
import kunlun.util.CloseUtils;
import kunlun.util.ObjUtils;
import org.beetl.core.*;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.exception.ErrorInfo;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.core.resource.FileResourceLoader;
import org.beetl.core.resource.StringTemplateResourceLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * The beetl text renderer.
 * @see <a href="http://ibeetl.com/">Beetl</a>
 * @author Kahle
 */
public class BeetlTextRenderer extends AbstractTextRenderer {
    private final GroupTemplate groupTemplate;
    private ClasspathResourceLoader classpathLoader = new ClasspathResourceLoader();
    private FileResourceLoader fileLoader = new FileResourceLoader();

    public BeetlTextRenderer(GroupTemplate groupTemplate) {

        this.groupTemplate = Assert.notNull(groupTemplate);
    }

    public BeetlTextRenderer() {
        try {
            StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
            Configuration cfg = Configuration.defaultConfiguration();
            groupTemplate = new GroupTemplate(resourceLoader, cfg);
            groupTemplate.setErrorHandler(new ConsoleErrorHandler(){
                @Override
                public void processException(BeetlException ex, GroupTemplate groupTemplate, Writer writer) {
//                    super.processException(ex, groupTemplate, writer);

                    ErrorInfo error = new ErrorInfo(ex);
                    int line = error.errorTokenLine;

                    StringBuilder builder = new StringBuilder("\n>> ")
                            .append(DateUtils.format())
                            .append(": ").append(error.type)
                            .append(": ").append(error.errorTokenText)
                            .append("\n位于").append(line != 0 ? line + "行" : "").append(" 资源:")
//                            .append(getResourceName(ex.resource.getId()))
                            .append("\n")
                            ;

                    try {
                        @SuppressWarnings("rawtypes")
                        Resource res = ex.resource;
                        //显示前后三行的内容
                        int[] range = this.getRange(line);
                        String content = res.getContent(range[0], range[1]);
                        if (content != null) {
                            String[] strs = content.split(ex.cr);
                            int lineNumber = range[0];
                            for (String str : strs) {
                                builder.append(lineNumber);
                                builder.append("|");
                                builder.append(str).append("\n");
                                lineNumber++;
                            }
                        }
                    } catch (IOException e) {
                        // ingore
                    }

                    if (error.errorCode.equals(BeetlException.TEMPLATE_LOAD_ERROR)) {
                        if (error.msg != null) { builder.append(error.msg); }
                        builder.append("\n").append(groupTemplate.getResourceLoader().getInfo()).append("\n");
                    }

                    if (error.hasCallStack()) {
                        builder.append("  ========================").append("\n");
                        builder.append("  调用栈:").append("\n");
                        for (int i = 0; i < error.resourceCallStack.size(); i++) {
                            builder.append("  ")
                                    .append(error.resourceCallStack.get(i))
                                    .append(" 行：")
                                    .append(error.tokenCallStack.get(i).line)
                                    .append("\n");
                        }
                    }

                    throw new IllegalStateException(builder.toString(), ex);
                }
            });
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public GroupTemplate getGroupTemplate() {

        return groupTemplate;
    }

    public ClasspathResourceLoader getClasspathLoader() {

        return classpathLoader;
    }

    public void setClasspathLoader(ClasspathResourceLoader classpathLoader) {

        this.classpathLoader = Assert.notNull(classpathLoader);
    }

    public FileResourceLoader getFileLoader() {

        return fileLoader;
    }

    public void setFileLoader(FileResourceLoader fileLoader) {

        this.fileLoader = Assert.notNull(fileLoader);
    }

    @Override
    public void render(Object template, Object data, Object output) {
        if (template == null) { return; }
        Writer writer = (Writer) Assert.isInstanceOf(Writer.class, output);
        Reader reader = null;
        try {
            if (template instanceof String) {
                Template tp = groupTemplate.getTemplate(template);
                tp.binding(BeanUtils.beanToMap(data)); tp.renderTo(writer);
            }
            else if (template instanceof Reader) {
                Template tp = groupTemplate.getTemplate(IOUtils.toString(reader=(Reader)template));
                tp.binding(BeanUtils.beanToMap(data)); tp.renderTo(writer);
            }
            else if (template instanceof Tpl) {
                Tpl tpl = (Tpl) template;
                if (ObjUtils.isEmpty(tpl.getContent()) && getTemplateLoader() != null) {
                    getTemplateLoader().accept(tpl);
                }
                if (!ObjUtils.isEmpty(tpl.getContent())) {
                    render(tpl.getContent(), data, output); return;
                }
                String classpathName = "classpath://";
                String tplName = tpl.getName();
                if (tplName != null && tplName.startsWith(classpathName)) {
                    tplName = tplName.substring(classpathName.length());
                    Template tp = groupTemplate.getTemplate(tplName, getClasspathLoader());
                    tp.binding(BeanUtils.beanToMap(data));
                    tp.renderTo(writer);
                } else {
                    Template tp = groupTemplate.getTemplate(tplName, getFileLoader());
                    tp.binding(BeanUtils.beanToMap(data));
                    tp.renderTo(writer);
                }
            } else { throw new IllegalArgumentException("Unsupported template type! "); }
        } catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        } finally {
            CloseUtils.closeQuietly(reader);
            CloseUtils.closeQuietly(writer);
        }
    }

}
